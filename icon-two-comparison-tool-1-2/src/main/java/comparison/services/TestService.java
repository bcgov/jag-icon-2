package ca.bc.gov.open.comparison.services;

import ca.bc.gov.open.comparison.config.WebServiceSenderWithAuth;
import ca.bc.gov.open.icon.auth.*;
import ca.bc.gov.open.icon.myfiles.*;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Service
public class TestService {
    private final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    private final WebServiceSenderWithAuth webServiceSenderWithAuth;

    private final Javers javers = JaversBuilder.javers().build();

    @Autowired
    public TestService(WebServiceSenderWithAuth webServiceSenderWithAuth) {
        this.webServiceSenderWithAuth = webServiceSenderWithAuth;
    }

    @Value("${host.api-host}")
    private String apiHost;

    @Value("${host.wm-host}")
    private String wmHost;

    @Value("${host.username}")
    private String username;

    @Value("${host.password}")
    private String password;

    private PrintWriter fileOutput;
    private static String outputDir = "icon-two-comparison-tool-1-2/results/";

    private int overallDiff = 0;
    private int diffCounter = 0;

    enum eHasFunctionalAbility {
        csNum,
        serviceCd,
        functionCd,
        AuthoritativePartyIdentifier,
        BiometricsSignature
    }

    public void runCompares() throws Exception {

        //        getUserInfo_Compare();
        //        getDeviceInfo_Compare();
        //        getPreAuthorizeClient_Compare();
        getHasFunctionalAbility_Compare();
    }

    private void getUserInfo_Compare() throws Exception {

        diffCounter = 0;
        GetUserInfo getUserInfo = new GetUserInfo();
        UserInfoOut userInfoOut = new UserInfoOut();
        UserInfoInner userInfoInner = new UserInfoInner();

        var request = new UserInfo();
        getUserInfo.setXMLString(userInfoOut);
        userInfoOut.setUserInfo(userInfoInner);
        userInfoInner.setUserInfo(request);

        InputStream inputIds = getClass().getResourceAsStream("/getUserInfo.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput = new PrintWriter(outputDir + "getUserInfo.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.format("\nINFO: getUserInfo_Compare with csNum: %s\n", line);
            request.setCsNum(line);

            if (!compare(new GetUserInfoResponse(), getUserInfo, "information/user-info")) {
                fileOutput.format("\nINFO: getUserInfo_Compare with csNum: %s\n", line);
                ++diffCounter;
            }
        }

        printCompletion();
    }

    private void getDeviceInfo_Compare() throws Exception {

        diffCounter = 0;
        GetDeviceInfo getDeviceInfo = new GetDeviceInfo();
        DeviceInfoOut deviceInfoOut = new DeviceInfoOut();
        DeviceInfoInner deviceInfoInner = new DeviceInfoInner();

        var request = new DeviceInfo();
        getDeviceInfo.setXMLString(deviceInfoOut);
        deviceInfoOut.setDeviceInfo(deviceInfoInner);
        deviceInfoInner.setDeviceInfo(request);

        InputStream inputIds = getClass().getResourceAsStream("/getDeviceInfo.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput = new PrintWriter(outputDir + "getDeviceInfo.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.format("\nINFO: getDeviceInfo_Compare with csNum: %s\n", line);
            request.setCertificateName(line);

            if (!compare(new GetDeviceInfoResponse(), getDeviceInfo, "information/device-info")) {
                fileOutput.format("\nINFO: getDeviceInfo_Compare with csNum: %s\n", line);
                ++diffCounter;
            }
        }

        printCompletion();
    }

    private void getPreAuthorizeClient_Compare() throws Exception {

        diffCounter = 0;
        GetPreAuthorizeClient getPreAuthorizeClient = new GetPreAuthorizeClient();
        PreAuthorizeClientOut deviceInfoOut = new PreAuthorizeClientOut();
        PreAuthorizeClientInner deviceInfoInner = new PreAuthorizeClientInner();

        var request = new PreAuthorizeClient();
        getPreAuthorizeClient.setXMLString(deviceInfoOut);
        deviceInfoOut.setPreAuthorizeClient(deviceInfoInner);
        deviceInfoInner.setPreAuthorizeClient(request);

        InputStream inputIds = getClass().getResourceAsStream("/getPreAuthorizeClient.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput =
                new PrintWriter(outputDir + "getPreAuthorizeClient.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.format("\nINFO: getPreAuthorizeClient_Compare with csNum: %s\n", line);
            request.setCsNum(line);

            if (!compare(
                    new GetPreAuthorizeClientResponse(),
                    getPreAuthorizeClient,
                    "auth/pre-auth-client")) {
                fileOutput.format("\nINFO: getPreAuthorizeClient_Compare with csNum: %s\n", line);
                ++diffCounter;
            }
        }

        printCompletion();
    }

    private void getHasFunctionalAbility_Compare() throws Exception {

        diffCounter = 0;
        var getHasFunctionalAbility = new GetHasFunctionalAbility();

        var hasFunctionalAbilityOut = new HasFunctionalAbilityOut();
        var hasFunctionalAbilityInner = new HasFunctionalAbilityInner();
        var functionalAbility = new FunctionalAbility();
        var request = new HasFunctionalAbility();
        getHasFunctionalAbility.setXMLString(hasFunctionalAbilityOut);
        hasFunctionalAbilityOut.setHasFunctionalAbility(hasFunctionalAbilityInner);
        hasFunctionalAbilityInner.setHasFunctionalAbility(request);
        request.setFunctionalAbility(functionalAbility);

        var userTokenOut = new UserTokenOut();
        var userTokenInner = new UserTokenInner();
        var userToken = new UserToken();
        getHasFunctionalAbility.setUserTokenString(userTokenOut);
        userTokenOut.setUserToken(userTokenInner);
        userTokenInner.setUserToken(userToken);

        InputStream inputIds = getClass().getResourceAsStream("/getHasFunctionalAbility.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput =
                new PrintWriter(outputDir + "getHasFunctionalAbility.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(",");

            request.setCsNum(line[eHasFunctionalAbility.csNum.ordinal()]);
            functionalAbility.setServiceCd(line[eHasFunctionalAbility.serviceCd.ordinal()]);
            functionalAbility.setFunctionCd(line[eHasFunctionalAbility.functionCd.ordinal()]);

            userToken.setAuthoritativePartyIdentifier(
                    line[eHasFunctionalAbility.AuthoritativePartyIdentifier.ordinal()]);
            userToken.setBiometricsSignature(
                    line[eHasFunctionalAbility.BiometricsSignature.ordinal()]);
            userToken.setCSNumber(line[eHasFunctionalAbility.csNum.ordinal()]);

            System.out.format(
                    "\nINFO: getHasFunctionalAbility_Compare with csNum: %s, serviceCd: %s,  functionCd: %s,  AuthoritativePartyIdentifier: %s, BiometricsSignature: %s, CSNumber: %s\n",
                    line[0], line[1], line[2], line[3], line[4], line[0]);

            if (!compare(
                    new GetPreAuthorizeClientResponse(),
                    getHasFunctionalAbility,
                    "auth/has-functional-ability")) {
                fileOutput.format(
                        "\nINFO: getHasFunctionalAbility_Compare with csNum: %s, serviceCd: %s,  functionCd: %s,  AuthoritativePartyIdentifier: %s, BiometricsSignature: %s, CSNumber: %s\n",
                        line[0], line[1], line[2], line[3], line[4], line[0]);
                ++diffCounter;
            }
        }

        printCompletion();
    }

    private void printCompletion() {
        System.out.println(
                "########################################################\n"
                        + "INFO: getFileDetailCriminal Completed there are "
                        + diffCounter
                        + " diffs\n"
                        + "########################################################");

        fileOutput.println(
                "########################################################\n"
                        + "INFO: getFileDetailCriminal Completed there are "
                        + diffCounter
                        + " diffs\n"
                        + "########################################################");

        overallDiff += diffCounter;

        fileOutput.close();
    }

    public <T, G> boolean compare(T response, G request, String wsdl)
            throws SOAPException, Exception {

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();

        SaajSoapMessageFactory messageFactory =
                new SaajSoapMessageFactory(MessageFactory.newInstance("SOAP 1.2 Protocol"));
        messageFactory.setSoapVersion(SoapVersion.SOAP_12);
        messageFactory.afterPropertiesSet();

        webServiceTemplate.setMessageSender(webServiceSenderWithAuth);
        webServiceTemplate.setMessageFactory(messageFactory);

        jaxb2Marshaller.setContextPaths("ca.bc.gov.open.icon.audit", "ca.bc.gov.open.icon.auth");

        webServiceTemplate.setMarshaller(jaxb2Marshaller);
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller);
        webServiceTemplate.afterPropertiesSet();

        T resultObjectWM = null;
        T resultObjectAPI = null;

        try {
            resultObjectAPI = (T) webServiceTemplate.marshalSendAndReceive(apiHost, request);
            // resultObjectWM = (T) webServiceTemplate.marshalSendAndReceive(wmHost + wsdl,
            // request);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to send request... " + e);
            fileOutput.println("ERROR: Failed to send request... " + e);
        } finally {
            Thread.sleep(2000);
        }

        Diff diff = javers.compare(resultObjectAPI, resultObjectWM);

        String responseClassName = response.getClass().getName();
        if (diff.hasChanges() && printDiff(diff)) {
            return false;
        } else {
            if (resultObjectAPI == null && resultObjectWM == null) {
                System.out.println(
                        "WARN: "
                                + responseClassName.substring(
                                        responseClassName.lastIndexOf('.') + 1)
                                + ": NULL responses");
            } else {
                System.out.println(
                        "INFO: "
                                + responseClassName.substring(
                                        responseClassName.lastIndexOf('.') + 1)
                                + ": No Diff Detected");
            }
            return true;
        }
    }

    private boolean printDiff(Diff diff) {

        List<Change> actualChanges = new ArrayList<>(diff.getChanges());

        actualChanges.removeIf(
                c -> {
                    if (c instanceof ValueChange) {
                        return ((ValueChange) c).getLeft() == null
                                && ((ValueChange) c).getRight().toString().isBlank();
                    } else if (c instanceof ListChange) {
                        // we only compare value and do not want to show the array name
                        return true;
                    }

                    return false;
                });

        int diffSize = actualChanges.size();

        if (diffSize == 0) {
            return false;
        }

        String[] header = new String[] {"Property", "API Response", "WM Response"};
        String[][] table = new String[diffSize + 1][3];
        table[0] = header;

        for (int i = 0; i < diffSize; ++i) {
            Change ch = actualChanges.get(i);

            if (ch instanceof ListChange) {
                String apiVal =
                        ((ListChange) ch).getLeft() == null
                                ? "null"
                                : ((ListChange) ch).getLeft().toString();
                String wmVal =
                        ((ListChange) ch).getRight() == null
                                ? "null"
                                : ((ListChange) ch).getRight().toString();
                table[i + 1][0] = ((ListChange) ch).getPropertyNameWithPath();
                table[i + 1][1] = apiVal;
                table[i + 1][2] = wmVal;
            } else if (ch instanceof ValueChange) {
                String apiVal =
                        ((ValueChange) ch).getLeft() == null
                                ? "null"
                                : ((ValueChange) ch).getLeft().toString();
                String wmVal =
                        ((ValueChange) ch).getRight() == null
                                ? "null"
                                : ((ValueChange) ch).getRight().toString();
                table[i + 1][0] = ((ValueChange) ch).getPropertyNameWithPath();
                table[i + 1][1] = apiVal;
                table[i + 1][2] = wmVal;
            }
        }

        boolean leftJustifiedRows = false;
        int totalColumnLength = 10;
        /*
         * Calculate appropriate Length of each column by looking at width of data in
         * each column.
         *
         * Map columnLengths is <column_number, column_length>
         */
        Map<Integer, Integer> columnLengths = new HashMap<>();
        Arrays.stream(table)
                .forEach(
                        a ->
                                Stream.iterate(0, (i -> i < a.length), (i -> ++i))
                                        .forEach(
                                                i -> {
                                                    if (columnLengths.get(i) == null) {
                                                        columnLengths.put(i, 0);
                                                    }
                                                    if (columnLengths.get(i) < a[i].length()) {
                                                        columnLengths.put(i, a[i].length());
                                                    }
                                                }));

        for (Map.Entry<Integer, Integer> e : columnLengths.entrySet()) {
            totalColumnLength += e.getValue();
        }
        fileOutput.println("=".repeat(totalColumnLength));
        System.out.println("=".repeat(totalColumnLength));

        final StringBuilder formatString = new StringBuilder("");
        String flag = leftJustifiedRows ? "-" : "";
        columnLengths.entrySet().stream()
                .forEach(e -> formatString.append("| %" + flag + e.getValue() + "s "));
        formatString.append("|\n");

        Stream.iterate(0, (i -> i < table.length), (i -> ++i))
                .forEach(
                        a -> {
                            fileOutput.printf(formatString.toString(), table[a]);
                            System.out.printf(formatString.toString(), table[a]);
                        });

        fileOutput.println("=".repeat(totalColumnLength));
        System.out.println("=".repeat(totalColumnLength));

        return true;
    }
}
