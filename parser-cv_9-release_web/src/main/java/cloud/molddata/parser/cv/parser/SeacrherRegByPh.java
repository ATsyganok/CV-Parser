package cloud.molddata.parser.cv.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SeacrherRegByPh {

    public String[] searcherCountryCode(String requestPhone){
        String[] result = new String[3];
        String[] resultNone = new String[3];
        String tempCountryCode = null;
        requestPhone = requestPhone.replaceAll("\\s*[\\(*|\\)*|\\s*]+\\s*", "");
        String path = findFileCountryCode(requestPhone);
        JSONCC[] jsoNmy = parseCountryCode(path);

        for (int j = 6;j>1;--j){
            if (requestPhone.length()<j) //for phone codes with digits less than 6
                continue;
            tempCountryCode = requestPhone.substring(0,j);
            for (int i = 0; i<jsoNmy.length;++i){
                if (jsoNmy[i].mask.contentEquals(tempCountryCode)){
                    result[0]=jsoNmy[i].mask;
                    result[1]=jsoNmy[i].name_en;
                    result[2]=jsoNmy[i].desc_en;
                    return result;
                }
            }
        }
        return new String[]{(resultNone[0] = requestPhone),(resultNone[1] = "unknown"),(resultNone[2] = "unknown")};
    }

    public String findFileCountryCode(String requestPhone){
        String path;
        if (!requestPhone.matches("\\+\\d+")){
            requestPhone="+"+requestPhone;
        }
        switch (requestPhone.substring(1, 2)){
            case "1":
                path = "/opt/tomcat/temp/location/phones-us.json";
                break;
            case "7":
                path = "/opt/tomcat/temp/location/phones-ru.json";
                break;
            default:
                path = "/opt/tomcat/temp/location/phone-codes.json";
                break;
        }
        return path;
    }

    private JSONCC[] parseCountryCode(String path){

        String resultJSON = null;

        try {
            resultJSON = RequestToCodesList(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().create();
        JSONCC[] jsoNmy = gson.fromJson(resultJSON, JSONCC[].class);
        return jsoNmy;
    }

    private static String RequestToCodesList(String path) throws IOException {
        //ClassLoader classLoader = new SeacrherRegByPh().getClass().getClassLoader();
        //File file = ResourceUtils.getFile(classLoader.getResource(path).getFile());
        File file = new File(path);
        FileReader fr = new FileReader(file);
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(fr);
            char[] buf = new char[1000000];

            int r = 0;
            do {
                if ((r = br.read(buf)) > 0)
                    sb.append(new String(buf, 0, r));
            } while (r > 0);
        } finally {
            fr.close();
        }

        return sb.toString();
    }

}
