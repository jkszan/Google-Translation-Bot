
//Import Statements

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;


public class Translator {

    //Hashmap that ties the full English names of languages to their accepted prefix values
    private HashMap<String, String> languages = new HashMap<>();

    //Constructor that initiates the language hashmap
        public Translator(){
            init();
        }

        //Populates languages hashmap
        private void init(){
            languages.put("afrikaans", "af");
            languages.put("irish", "ga");
            languages.put("albanian", "sq");
            languages.put("italian", "it");
            languages.put("arabic", "ar");
            languages.put("japanese", "ja");
            languages.put("azerbaijani", "az");
            languages.put("korean", "ko");
            languages.put("latin", "la");
            languages.put("chinese-simplified", "zh-CN");
            languages.put("chinese-traditional", "zh-TW");
            languages.put("polish", "pl");
            languages.put("portuguese", "pt");
            languages.put("english", "en");
            languages.put("german", "de");
            languages.put("spanish", "es");
            languages.put("french", "fr");
            languages.put("slovenian", "sl");
        }

        //Tests if a language exists in the languages hashmap
        public boolean testLanguage(String lang){
            return languages.containsKey(lang.toLowerCase());
        }

        //Gets the accepted prefix values of a language based on the full English name
        private String parseLanguage(String fullLanguage){
            return languages.get(fullLanguage);
        }

        //Main translation method
        public String translate(String langFromInit, String langToInit, String text) throws IOException {

            //Declaring instance variables langFrom and langTo for use in the translation
            String langFrom;
            String langTo;

            //Formats language inputs to all lowercase, this allows users to type language names in any casing
            langFromInit = langFromInit.toLowerCase();
            langToInit = langToInit.toLowerCase();


            //Checks if languages exist in the languages hashmap. Allows "" as a field for the origin language
            //The "" field for the origin language corresponds to "Detect Language"
            //This throws IllegalArgumentException if languages do not exist
            if((!testLanguage(langFromInit) && langFromInit != "") || !testLanguage(langToInit)){
                throw new IllegalArgumentException();
            }

            //Changes languages from a full English name to the accepted prefixes
            langTo = parseLanguage(langToInit);
            //Does not perform parseLanguage on an empty string to avoid errors when trying to use detectLanguageTranslate
            if(langFromInit != "") {
                langFrom = parseLanguage(langFromInit);
            }
            else{
                langFrom = "";
            }

            //URL of my google scripts webapp that will proxy a translation from Google Translate
            String urlStr = "https://script.google.com/macros/s/AKfycbxdEqXQnSawcnTQ7WRuNI_KvHmWnooQtChyhWewwfQfD5Cb8osX/exec" +
                    "?q=" + URLEncoder.encode(text, "UTF-8") +
                    "&target=" + langTo +
                    "&source=" + langFrom;

            URL webapp = new URL(urlStr);
            
            //Opens connection and requests information
            StringBuilder response = new StringBuilder();
            HttpURLConnection con = (HttpURLConnection) webapp.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            //Creates an input reader that gets the response to the Google Translate query, then returns the translated String
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }

    }


