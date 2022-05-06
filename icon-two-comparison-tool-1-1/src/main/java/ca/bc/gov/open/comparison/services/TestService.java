package ca.bc.gov.open.comparison.services;

import ca.bc.gov.open.comparison.config.WebServiceSenderWithAuth;
import ca.bc.gov.open.icon.myfiles.*;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private static String outputDir = "icon-two-comparison-tool-1-1/results/";

    private int overallDiff = 0;
    private int diffCounter = 0;

    public void runCompares() throws Exception {
        MyFiles();
    }

    public void MyFiles() throws Exception {
        System.out.println("INFO: ICON2 MyFiles Diff testing started");
        // getAgencyFile_Compare();
        // getClientClaimsCompare();
        // getCsNumsByDateCompare();
        getClientInfoCompare();
    }

    private void getAgencyFile_Compare() throws Exception {
        diffCounter = 0;

        var request = new GetAgencyFile();
        InputStream inputIds = getClass().getResourceAsStream("/getAgencyFile.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput = new PrintWriter(outputDir + "getAgencyFile.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(",");
            String agencyIdCd = line[0], agencyFileNo = line[1];
            System.out.format(
                    "\nINFO: getAgencyFile with agencyIdCd: %s, agencyFileNo: %s\n",
                    agencyIdCd, agencyFileNo);
            request.setAgencyIdCd(agencyIdCd);
            request.setAgencyFileNo(agencyFileNo);
            if (!compare(new GetAgencyFileResponse(), request, "files/agency-file")) {
                fileOutput.format(
                        "\nINFO: getAgencyFile with agencyIdCd: %s, agencyFileNo: %s\n",
                        agencyIdCd, agencyFileNo);
                ++diffCounter;
            }
        }

        printCompletion();
    }

    private void getClientClaimsCompare() throws Exception {
        diffCounter = 0;
        GetClientClaims request = new GetClientClaims();

        InputStream inputIds = getClass().getResourceAsStream("/getClientClaims.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput = new PrintWriter(outputDir + "getClientClaims.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.format("\nINFO: getClientClaimsCompare with directedIdentifier: %s\n", line);
            request.setDirectedIdentifier(line);

            if (!compare(new GetClientClaimsResponse(), request, "files/claims")) {
                fileOutput.format(
                        "\nINFO: getClientClaimsCompare with directedIdentifier: %s\n", line);
                ++diffCounter;
            }
        }

        printCompletion();
    }

    private void getCsNumsByDateCompare() throws Exception {
        diffCounter = 0;
        GetCsNumsByDate request = new GetCsNumsByDate();

        fileOutput = new PrintWriter(outputDir + "getCsNumsByDate.txt", StandardCharsets.UTF_8);

        System.out.format("\nINFO: getCsNumsByDateCompare\n");
        request.setStartDate("1990-01-01 00:00:00");
        request.setEndDate(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.of("UTC"))
                        .format(Instant.now()));

        if (!compare(new GetClientClaimsResponse(), request, "files/csnumsbydate")) {
            fileOutput.format("\nINFO: getCsNumsByDateCompare\n");
            ++diffCounter;
        }

        printCompletion();
    }

    private void getClientInfoCompare() throws Exception {
        diffCounter = 0;
        GetClientInfo request = new GetClientInfo();

        InputStream inputIds = getClass().getResourceAsStream("/getClientInfo.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput = new PrintWriter(outputDir + "getClientInfo.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.format("\nINFO: getClientInfoCompare with csNum: %s\n", line);
            request.setCsNum(line);

            if (!compare(new GetClientInfoResponse(), request, "files/client-info")) {
                fileOutput.format("\nINFO: getClientInfoCompare with csNum: %s\n", line);
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
                new SaajSoapMessageFactory(MessageFactory.newInstance("SOAP 1.1 Protocol"));
        messageFactory.afterPropertiesSet();

        webServiceTemplate.setMessageSender(webServiceSenderWithAuth);
        webServiceTemplate.setMessageFactory(messageFactory);

        jaxb2Marshaller.setContextPaths("ca.bc.gov.open.icon.myfiles");

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
