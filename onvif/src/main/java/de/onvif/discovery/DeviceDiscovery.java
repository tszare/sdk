package de.onvif.discovery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.onvif.utils.OnvifUtils;
import regis.http.client.RegisUtil;

/**
 * Device discovery class to list local accessible devices probed per UDP probe messages.
 *
 * @author th
 * @version 0.1
 * @date 2015-06-18
 */
@SuppressWarnings({ "unused" })
public class DeviceDiscovery {
	private static final Logger log = LoggerFactory.getLogger(DeviceDiscovery.class);

	public static final String WS_DISCOVERY_SOAP_VERSION = "SOAP 1.2 Protocol";
	public static final String WS_DISCOVERY_CONTENT_TYPE = "application/soap+xml";
	// public static final int WS_DISCOVERY_TIMEOUT = 8000;

	/** IPv6 not supported yet. set enableIPv6 to true for testing if you need IP6 discovery. */
	public static final boolean enableIPv6 = false;

	public static final String WS_DISCOVERY_ADDRESS_IPv6 = "[FF02::C]";
	public static final String WS_DISCOVERY_PROBE_MESSAGE = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:tns=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"><soap:Header><wsa:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</wsa:Action><wsa:MessageID>urn:uuid:c032cfdd-c3ca-49dc-820e-ee6696ad63e2</wsa:MessageID><wsa:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</wsa:To></soap:Header><soap:Body><tns:Probe/></soap:Body></soap:Envelope>";
	private static final Random random = new SecureRandom();

	public static void main(String[] args) throws InterruptedException {
		for (URL url : discoverWsDevicesAsUrls()) {
			System.out.println("Device discovered: " + url.toString());
		}
	}

	/**
	 * Discover WS device on the local network and returns Urls
	 *
	 * @return list of unique device urls
	 */
	public static Collection<URL> discoverWsDevicesAsUrls() {
		return discoverWsDevicesAsUrls("", "");
	}

	/**
	 * Discover WS device on the local network with specified filter
	 *
	 * @param regexpProtocol url protocol matching regexp like "^http$", might be empty ""
	 * @param regexpPath url path matching regexp like "onvif", might be empty ""
	 * @return list of unique device urls filtered
	 */
	public static Collection<URL> discoverWsDevicesAsUrls(String regexpProtocol, String regexpPath) {
		final Collection<URL> urls = new TreeSet<>(new Comparator<URL>() {
			@Override
			public int compare(URL o1, URL o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		Collection<String> keyList = discoverWsDevices();
		for (String key : keyList) {
			log.info("wsService Url >>>" + key);
		}

		for (String key : keyList) {
			try {
				final URL url = new URL(key);
				boolean ok = true;
				// if (regexpProtocol.length() > 0 &&
				// !url.getProtocol().matches(regexpProtocol)) {
				// ok = false;
				// }
				if (regexpPath.length() > 0 && !url.getPath().matches(regexpPath)) {
					ok = false;
				}
				// ignore ip6 hosts
				if (ok && !enableIPv6 && url.getHost().startsWith("[")) {
					ok = false;
				}
				if (ok) {
					urls.add(url);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return urls;
	}

	public static List<InetAddress> getAddressList() {

		final List<InetAddress> addressList = Lists.newArrayList();
		try {
			final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces != null) {
				while (interfaces.hasMoreElements()) {
					NetworkInterface anInterface = interfaces.nextElement();
					if (!anInterface.isLoopback()) {
						final List<InterfaceAddress> interfaceAddresses = anInterface.getInterfaceAddresses();
						for (InterfaceAddress address : interfaceAddresses) {
							// Class clz = address.getAddress().getClass();

							if (!enableIPv6 && address.getAddress() instanceof Inet6Address) {
								continue;
							}
							addressList.add(address.getAddress());
						}
					}
				}
			}
		} catch (SocketException e) {
			log.error("fatalError", e);
		}
		return addressList;
	}

	private static Collection<Node> getNodeMatching(Node body, String regexp) {
		final Collection<Node> nodes = new ArrayList<>();
		if (body.getNodeName().matches(regexp)) {
			nodes.add(body);
		}
		if (body.getChildNodes().getLength() == 0) {
			return nodes;
		}
		NodeList returnList = body.getChildNodes();
		for (int k = 0; k < returnList.getLength(); k++) {
			final Node node = returnList.item(k);
			nodes.addAll(getNodeMatching(node, regexp));
		}
		return nodes;
	}

	public static Collection<String> parseSoapResponseForUrls(byte[] data) throws SOAPException, IOException {
		// log.info(new String(data));
		final Collection<String> urls = new ArrayList<>();
		MessageFactory factory = MessageFactory.newInstance(WS_DISCOVERY_SOAP_VERSION);
		final MimeHeaders headers = new MimeHeaders();
		headers.addHeader("Content-type", WS_DISCOVERY_CONTENT_TYPE);
		SOAPMessage message = factory.createMessage(headers, new ByteArrayInputStream(data));
		SOAPBody body = message.getSOAPBody();
		for (Node node : getNodeMatching(body, ".*:XAddrs")) {
			if (node.getTextContent().length() > 0) {
				urls.addAll(Arrays.asList(node.getTextContent().split(" ")));
			}
		}
		return urls;
	}

	public static class UdpRecvThread extends Thread {
		final DatagramSocket serverSocket;
		final CountDownLatch socketFinished;
		final AtomicInteger counter;

		final List<String> addressList;

		public UdpRecvThread(DatagramSocket socket, CountDownLatch latch, AtomicInteger counter1,
							 List<String> addrList) {
			this.serverSocket = socket;
			this.socketFinished = latch;
			this.counter = counter1;
			this.addressList = addrList;
		}

		@Override
		public void run() {
			log.info(this.getName() + " started");
			final DatagramPacket packet = new DatagramPacket(new byte[65536], 65536);
			// serverSocket.setSoTimeout(WS_DISCOVERY_TIMEOUT);
			try {
				serverSocket.setSoTimeout(500);
			} catch (SocketException e1) {
				log.info(e1.getMessage());
			}
			while (counter.get() > 0) {
				try {
					serverSocket.receive(packet);
					byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
					// log.info(new String(data));
					final Collection<String> collection = DeviceDiscovery.parseSoapResponseForUrls(data);
					for (String key : collection) {
						// log.info("Address Url>>>" + key);
						synchronized (this.addressList) {
							if (!addressList.contains(key)) {
								addressList.add(key);
							}
						}
					}
				} catch (Exception e) {
					// log.error("fatalError", e);
					log.info(e.getMessage());
				}

			}
			socketFinished.countDown();
			log.info("This recv thread is finished!!");
		}

	}

	/**
	 * Discover WS device on the local network
	 *
	 * @return list of unique devices access strings which might be URLs in most cases
	 */
	public static List<String> discoverWsDevices() {

		final List<String> serviceList = Lists.newArrayList();

		List<String> textLines = OnvifUtils.getResource("wsdl.txt");

		List<InetAddress> addressList = DeviceDiscovery.getAddressList();
		ExecutorService executorService = Executors.newCachedThreadPool();

		final int addressListSize = addressList.size();
		final int wsPort = 3702;
		final String wsIp = "239.255.255.250";
		final CountDownLatch threadFinished = new CountDownLatch(addressListSize);
		for (final InetAddress address : addressList) {
			log.info("current address is " + address.toString());
			final CountDownLatch socketFinished = new CountDownLatch(2);
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						final String uuid = UUID.randomUUID().toString();
						final String uuid2 = UUID.randomUUID().toString();
						final int port = random.nextInt(20000) + 40000;
						final DatagramSocket serverSocket = new DatagramSocket(port, address);
						final AtomicInteger syncCounter = new AtomicInteger(1);
						UdpRecvThread thread = new UdpRecvThread(serverSocket, socketFinished, syncCounter,
								serviceList);
						thread.setName("T" + address.toString() + "@" + RegisUtil.uuid8());
						thread.start();

						if (address instanceof Inet4Address) {

							InetAddress addr = InetAddress.getByName(wsIp);
							for (int i = 0; i < 3; i++) {
								for (String line : textLines) {
									// final String probe =
									// line.replaceAll("<a:MessageID>uuid:.*</wsa:MessageID>",
									// "<wsa:MessageID>urn:uuid:" + uuid +
									// "</a:MessageID>");
									String probe = line.replaceAll("<a:MessageID>uuid:.*</a:MessageID>",
											"<a:MessageID>uuid:" + uuid + "</a:MessageID>");

									probe = probe.replaceAll("<wsa:MessageID>urn:uuid:.*</wsa:MessageID>",
											"<wsa:MessageID>urn:uuid:" + uuid + "</wsa:MessageID>");

									probe = probe.replaceAll("<wsa:Address>urn:uuid:.*</wsa:Address>",
											"<wsa:Address>urn:uuid:" + uuid2 + "</wsa:Address>");
									// log.info(probe);
									byte[] bytes = probe.getBytes(StandardCharsets.UTF_8);
									DatagramPacket dp1 = new DatagramPacket(bytes, bytes.length, addr, wsPort);
									serverSocket.send(dp1);
									Thread.sleep(100);
								}

							}
							socketFinished.countDown();
							syncCounter.decrementAndGet();
						} else {
							log.info("Not Ipv4 >>> " + address.toString());
							socketFinished.countDown();
							syncCounter.decrementAndGet();
						}
						try {
							socketFinished.await();
						} catch (InterruptedException e) {
							log.info(e.getMessage());
						}
						serverSocket.close();
						log.info("close serverSocket");
						threadFinished.countDown();
					} catch (Exception e) {
						log.error("fatalError", e);
					}
				}
			};
			executorService.submit(runnable);
		}
		try {
			threadFinished.await();
			log.info("close executorService");
			executorService.shutdown();
			executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ignored) {
		}
		log.info("******Found Addr List**********\r\n" + Joiner.on("\r\n").join(serviceList));
		return serviceList;
	}

}
