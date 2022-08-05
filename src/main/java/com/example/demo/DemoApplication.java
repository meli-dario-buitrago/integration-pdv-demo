package com.example.demo;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.point.*;
import com.mercadopago.client.point.OperatingMode;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.point.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DemoApplication {

	private static String banner =
			"====================================================================================================" +
			"===================================================\n\n" +
			"█████             █████                                           █████     ███             " +
			"                            █████████   ███████████  █████\n" +
			"░░███             ░░███                                           ░░███     ░░░                     " +
			"                    ███░░░░░███ ░░███░░░░░███░░███ \n" +
			" ░███  ████████   ███████    ██████   ███████ ████████   ██████   ███████   ████   ██████  ████████ " +
			"                   ░███    ░███  ░███    ░███ ░███ \n" +
			" ░███ ░░███░░███ ░░░███░    ███░░███ ███░░███░░███░░███ ░░░░░███ ░░░███░   ░░███  ███░░███░░███░░███" +
			"     ██████████    ░███████████  ░██████████  ░███ \n" +
			" ░███  ░███ ░███   ░███    ░███████ ░███ ░███ ░███ ░░░   ███████   ░███     ░███ ░███ ░███ ░███ ░███" +
			"    ░░░░░░░░░░     ░███░░░░░███  ░███░░░░░░   ░███ \n" +
			" ░███  ░███ ░███   ░███ ███░███░░░  ░███ ░███ ░███      ███░░███   ░███ ███ ░███ ░███ ░███ ░███ ░███" +
			"                   ░███    ░███  ░███         ░███ \n" +
			" █████ ████ █████  ░░█████ ░░██████ ░░███████ █████    ░░████████  ░░█████  █████░░██████  ████ " +
			"█████                  █████   █████ █████        █████\n" +
			"░░░░░ ░░░░ ░░░░░    ░░░░░   ░░░░░░   ░░░░░███░░░░░      ░░░░░░░░    ░░░░░  ░░░░░  ░░░░░░  ░░░░ ░░░░░" +
			"                  ░░░░░   ░░░░░ ░░░░░        ░░░░░ \n" +
			"                                     ███ ░███                                                       " +
			"                                                   \n" +
			"                                    ░░██████                                                        " +
			"                                                   \n" +
			"                                     ░░░░░░                                                         " +
			"                                                   \n" +
			"\n====================================================================================================" +
			"===================================================\n";

	private static Scanner consoleReader = new Scanner(System.in);
	private static PointClient client = new PointClient();
	private static PointDevice device;
	private static PointPaymentIntent pointPaymentIntent;


	public static void main(String[] args) {
		System.out.println(banner);
		MercadoPagoConfig.setAccessToken("YOUR_PRODUCTIVE_ACCESS_TOKEN");
		selectMainMenu();
	}

	public static void printMainMenu() {
		System.out.println("\nSeleccione una opción");
		System.out.println("======================================================\n1. Listar dispositivos\n2. " +
				"Activar modo PDV\n3. Desactivar modo PDV\n4. Crear Intento de pago\n5. Buscar intentos de pago por " +
				"rango de fechas\n0. Salir\n======================================================");
	}

	public static void selectMainMenu(){
		printMainMenu();
		String option = consoleReader.next();
		while (!option.equals("0")){
			try {
				switch (option) {
					case "1":
						listDevices();
						break;
					case "2":
						changeOperatingMode(true);
						break;
					case "3":
						changeOperatingMode(false);
						break;
					case "4":
						createPaymentIntent();
						selectIntentMenu();
						break;
					case "5":
						searchPaymentIntentsByDates();
						break;
					case "6":
						cancelPaymentIntent();
						break;
					default:
						System.out.println("La opción no existe");
				}
			} catch (MPApiException apiException) {
				System.out.printf("Status Code: %s%n", apiException.getStatusCode());
				System.out.printf("Content: %s%n", apiException.getApiResponse().getContent());
			} catch (MPException  exception) {
				exception.printStackTrace();
			}
			printMainMenu();
			option = consoleReader.next();
		}
	}

	public static void printIntentMenu(){
		System.out.println("\nSeleccione una opción");
		System.out.println("==================================================");
		System.out.println("1. Obtener intento de pago\n2. Consultar estado de intento de pago\n3. Cancelar " +
				"Intento de pago\n9. Volver al menú principal");
		System.out.println("==================================================");
	}

	public static void selectIntentMenu() {
		printIntentMenu();
		String option = consoleReader.next();
		while (!option.equals("9")){
			try {
				switch (option) {
					case "1":
						getPaymentIntent();
						break;
					case "2":
						getPaymentIntentStatus();
						break;
					case "3":
						cancelPaymentIntent();
						break;
					default:
						System.out.println("La opción no existe");
				}
			} catch (MPApiException apiException) {
				System.out.printf("Status Code: %s%n", apiException.getStatusCode());
				System.out.printf("Content: %s%n", apiException.getApiResponse().getContent());
			} catch (MPException exception) {
				exception.printStackTrace();
			}
			printIntentMenu();
			option = consoleReader.next();
		}
	}

	public static void listDevices() throws MPException, MPApiException {
		Map<String, Object> devicesfilters = new HashMap<>();
		//devicesfilters.put("store_id", "47762646");
		//devicesfilters.put("pos_id", 49476225);

		MPSearchRequest request = MPSearchRequest.builder().limit(50).offset(0).filters(devicesfilters).build();
		PointDevices devices = client.getDevices(request);
		devices.getDevices().forEach(d -> {
			System.out.printf("Device Id: %s\n", d.getId());
			System.out.printf("Operating Mode: %s\n", d.getOperatingMode());
		});

		device = devices.getDevices().get(0);
	}

	public static void changeOperatingMode(boolean activate) throws MPException, MPApiException {
		PointDeviceOperatingModeRequest operatingModeRequest =
				PointDeviceOperatingModeRequest.builder().operatingMode(activate ? OperatingMode.PDV : OperatingMode.STANDALONE).build();
		PointDeviceOperatingMode deviceOperatingMode =
				client.changeDeviceOperatingMode(device.getId(), operatingModeRequest);
		System.out.printf("Device Operation Mode: %s\n", deviceOperatingMode.getOperatingMode());
	}

	public static void createPaymentIntent() throws MPException, MPApiException {
		PointPaymentIntentRequest paymentIntentRequest = PointPaymentIntentRequest.builder()
				.amount(new BigDecimal(100))
				.additionalInfo(PointPaymentIntentAdditionalInfoRequest.builder()
					.externalReference("4561ads-das4das4-das4754-das456")
					.printOnTerminal(true)
					// Campo ticket number solo para MLA
					.ticketNumber("123456")
					.build())
				/* Campos MLB
				.description("description")
				.payment(PointPaymentIntentPaymentRequest.builder()
                        .installments(1)
                        .installmentsCost("seller")
                        .type("credit_card")
                        .build())*/
				.build();

		pointPaymentIntent =
				client.createPaymentIntent(device.getId(), paymentIntentRequest);
		System.out.printf("Payment Intent Id: %s\n", pointPaymentIntent.getId());
		System.out.printf("Payment Amount: %s\n", pointPaymentIntent.getAmount());
		//System.out.printf("Payment Description: %s\n", pointPaymentIntent.getDescription());
	}

	public static void getPaymentIntent() throws MPException, MPApiException {
		PointSearchPaymentIntent searchPaymentIntent =
				client.searchPaymentIntent(pointPaymentIntent.getId());
		System.out.printf("Payment Intent Id: %s\n", searchPaymentIntent.getId());
		System.out.printf("Payment Status: %s\n", searchPaymentIntent.getState());
		System.out.printf("Payment Amount: %s\n", searchPaymentIntent.getAmount());
		//System.out.printf("Payment Description: %s\n", searchPaymentIntent.getDescription());
		if (searchPaymentIntent.getPayment() != null) {
			System.out.printf("Payment ID: %s\n", searchPaymentIntent.getPayment().getId());
		}
	}

	public static void getPaymentIntentStatus() throws MPException, MPApiException {
		PointStatusPaymentIntent paymentIntentStatus =
				client.getPaymentIntentStatus(pointPaymentIntent.getId());
		System.out.printf("Payment Intent Id: %s\n", pointPaymentIntent.getId());
		System.out.printf("Payment Intent Status: %s\n", paymentIntentStatus.getStatus());
	}

	public static void cancelPaymentIntent() throws MPException, MPApiException {
		PointCancelPaymentIntent cancelPaymentIntent =
				client.cancelPaymentIntent(device.getId(), pointPaymentIntent.getId());
		System.out.printf("Payment Intent Id: %s\n", pointPaymentIntent.getId());
		System.out.printf("Payment Intent Id Cancelled: %s\n", cancelPaymentIntent.getId());
	}

	public static void searchPaymentIntentsByDates() throws MPException, MPApiException {
		PointPaymentIntentListRequest intentListRequest =
				PointPaymentIntentListRequest.builder().startDate(LocalDate.of(2022, 8, 5))
						.endDate(LocalDate.of(2022, 8, 6)).build();
		PointPaymentIntentList paymentIntentList = client.getPaymentIntentList(intentListRequest);
		System.out.println("\n" + paymentIntentList.getEvents().size() + " intents:\n");
		paymentIntentList.getEvents().forEach(i -> {
			System.out.printf("Payment Intent Id: %s\n", i.getPaymentIntentId());
			System.out.printf("Status: %s\n", i.getStatus());
			System.out.printf("Created On: %s\n", i.getCreatedOn());
		});
	}
}
