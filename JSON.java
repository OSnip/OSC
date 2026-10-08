package OSC;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class JSON {
    public static Map<String,Object> importToHashMap (String path) {
        /*Cette fonction convertie et importe un fichier JSON en hashmap */

        //Déclaration des variables
        String content;
        Map<String,Object> result = new HashMap<String,Object>();
        int i=1;
        int v=0;
        String key = "";
        StringBuilder subContent = new StringBuilder();
        if (path.charAt(0) == '0') {
            //On récupère les cas récursifs
            content = path.substring(1);
        }
        else {
            //On lis les données du fichier JSON à l'adresse 'path'
            content = "";
            try {
                content = new String(Files.readAllBytes(Paths.get(path)));
            }
            catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
        int len = content.length();

        //Code principale
        while (i < len) {
            //Cas récursif
            if (content.charAt(i) == '{') {
                //On met en place la transmission de la chaine réduite pour la recursion
                String str = "0"+content.substring(i,len);
                Map<String,Object> newSubResult = importToHashMap(str);

                //On interprète le résultat en retirant les traces
                i += (Integer) newSubResult.get("ALLO") + 1;
                newSubResult.remove("ALLO");
                result.put(key,newSubResult);

                //On évite la répétition du 'put' avec le séparateur principal en le sautant
                if (content.charAt(i) == ',') {
                    i++;
                }
                continue;
            }

            //Cas de base
            if (content.charAt(i) == '}') {
                result.put("ALLO",i);
                i++;
                break;
            }
            else if (content.charAt(i) == '\"') {
                v = (v + 1) % 2;
                i++;
                continue;
            }
            else if (content.charAt(i) == ',' && v == 0) {
                //Séparateur principal
                result.put(key, subContent.toString());
                subContent = new StringBuilder();
            }
            else if (content.charAt(i) == ':' && v == 0) {
                //Séparateur secondaire
                key = subContent.toString();
                subContent = new StringBuilder();
            }
            else {
                //Lecture de la chaine
                subContent.append(content.charAt(i));
            }
            i++;
        }
		//Instructions évitant les problèmes liés à la fin du traitement
        if (subContent.length() > 0) {
            result.put(key, subContent.toString());
        }
        if (i == len) {
            result.remove("ALLO");
        }
        return result;
    }
}
