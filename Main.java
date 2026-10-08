package OSC;
/* INSTRUCTIONS CONSOLE
Chemin :
	D:\Programs\Java\Projets
Conpilation :
	javac OSC/Main.java
Execution :
	java OSC/Main
*/

// Ce code a été écrit le 8/10/2025 par jeux avec NotePad++ 8.8.5 et JRE 1.8
// La version contenant le moteur gerant uniquement une arboresence de dictionnaire de dictionnaire sans interactions avec la console a été fini le 11/10/2025 à 11:30

/* Ce code a pour but de simuler un environnement où l'on pourrait naviguer dans la console et qui pourrait être vu comme une arboresence de fichier
Exemple : L'Alliance
Alliance/Korbo/Académie
Je précise qu'ici le but est que le code soit un moteur, il ne doit pas être spécifique.
Le code doit transformer une arboresence qu'il recoit sous forme d'objet (dictionnaire de dictionnaire) et rendre la navigation possible.
De plus, des actions seront possible à certains endroits. Exemple : Une fois dans l'académie, on s'assoie et on lit un livre.
*/

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Arrays;
import OSC.JSON;

class Main {
    // Cet attribut de class est la liste des positions parcourue DONT celle où se trouve le client
    static List<String> path = new ArrayList<String>();
    // Cet attribut de class est le dictionnaire contenant l'arboresence
    static World place = new World();

    public static void main (String[] args) {
        boolean flag = true;
        while (flag) {
            display();
            String[] entry = getEntry();
            if (entry[0].equalsIgnoreCase("move") && entry.length == 2) {
                System.out.println(entry[1]);
                move(entry[1]);
            }
            else if (entry[0].equalsIgnoreCase("cat") && entry.length == 1) {
                cat();
            }
            else if (entry[0].equalsIgnoreCase("cd") && entry.length > 1) {
                String[] newPath = entry[1].split("/");
                cd(newPath);
            }
            else if (entry[0].equalsIgnoreCase("leave") && entry.length == 1) {
                flag = false;
            }
            else {
                System.out.println("Saisie incorrecte");
            }
        }

    }

    private static String[] getEntry () {
        Scanner source = new Scanner(System.in);
        String str = source.nextLine();
        return str.split(" ");
    }

    private static void display () {
        /* Affichage du contenu du chemin parcouru */
        String message = "Root";
        if (path.isEmpty()) {message += "/";}
        for (String s : path) {
            message = message + "/" + s;
        }
        System.out.println("================ READY ================");
        System.out.println(message);
        if (path.isEmpty()) {System.out.println("================ ROOT =================");}
        else {System.out.println("============== DIRECTORY ==============");}
        String[] array = dir();
        if (array.length == 0) {
            System.out.println("Empty");
        }
        else {
            System.out.println("-+");
            for (String position : dir()) {
                System.out.println(" |	-> "+position);
            }
            System.out.println("-+");
        }

    }

    private static void move (String target) {
        /* Cette methode prend en entrée la cible voulue et deplace le client à l'endroit voulue */
        if (target.equalsIgnoreCase("back")) {
            // Retour direct vers le parent
            if (!path.isEmpty()) {
                path.remove(path.size()-1);
                if (path.isEmpty()) {
                    System.out.println("Retour vers : Base");
                }
                else {
                    System.out.println("Retour vers : "+path.get(path.size()-1));
                }
            }
            else {
                System.out.println("MoveError : Déplacement impossible, la cible n'existe pas.");
            }
        }
        else {
            // Déplacement vers la cible (sans retour)
            String[] possibleWay=dir();
            boolean succes = false;
            for (String way : possibleWay) {
                // On verifie chaque cible possible avec celle voulue
                if (way.equals(target)) {
                    path.add(target);
                    succes = true;
                    System.out.println("Déplacement vers : "+target);
                }
            }
            if (!succes) {
                System.out.println("MoveError : Déplacement impossible, la cible n'existe pas.");
            }
        }

    }

    private static String[] dir () {
        /* Cette fonction ne prend pas d'entrée et renvoie un tableau contenant les différentes déstinations possibles */

        // Initialisation des parametres de sorties
        List<String> outList;
        String[] out;

        if (whereAmI().equals("Base")) {
            // Traitement du premier rang de l'arboresence
            outList = Map_to_KeyList(place.univers);
        }
        else {
            // Traitement des autres rangs
            Map<String, Object> map = place.univers;
            int len = path.size();
            String value;

            // Indentification du lieu où se trouve le client
            for (int i = 0; i < len; i++) {
                // Teste de l'existence de la racine
                value = path.get(i);
                if (i == 0 && !Map_to_KeyList(place.univers).contains(value)) {
                    map = new HashMap<String,Object>();
                    map.put("null",null);
                    break;
                }
                if (map.get(value) == null || map.get(value) instanceof String) {
                    map = new HashMap<String,Object>();
                    // Gère les chemins erroné en renvoyant un dictionnaire avec pour clef null.
                    if (len > i+1) {
                        map.put("null",null);
                    }
                    break;
                }
                map = Object_to_Map(map.get(value));
            }
            outList = Map_to_KeyList(map);
        }

        //Préparation de la sortie, transformation de la liste en tableau
        out = new String[outList.size()];
        outList.toArray(out);

        return out;
    }

    private static String whereAmI () {
        /* Cette fonction n'a pas d'argument et renvoie la position du client */
        String out;
        if (path.isEmpty()) {
            out = "Base";
        }
        else {
            out = path.get(path.size()-1);
        }
        return out;
    }

    private static void cd (String[] newPath) {
        /*Cette methode prend en entrée le chemin voulu et y déplace le client*/

        // On autorise l'entrée "root" pour retourner à la racine
        if (newPath.length == 1 && newPath[0].equalsIgnoreCase("root")) {
            path.clear();
            return;
        }

        // On crée une copie de l'ancien chemin
        List<String> oldPath = new ArrayList<String>(path);

        // On remplace l'ancien chemin par le nouveau
        path.clear();
        path.addAll(Arrays.asList(newPath));

        // On vérifie le nouveau chemin

        if (dir().length != 0 && dir()[0].equals("null")) {
            // On rétablit l'ancien chemin
            path.clear();
            path.addAll(oldPath);
            System.out.println("DirectoryError : Déplacement impossible, le chemin est erroné.");
        }
    }

    private static void cat () {
        /* Cette fonction revoie le contenu du fichier actuel */
        String outMessage;
        if (dir().length == 0) {
            Map<String, Object> map = place.univers;
            // Indentification du lieu où se trouve le client
            for (String value : path) {
                if (map.get(value) == null) {
                    System.out.println("CatError : Impossible de localiser le lieu.");
                    break;
                }
                if (map.get(value) instanceof String) {
                    outMessage = Object_to_String(map.get(value));
                    System.out.println(outMessage);
                }
                map = Object_to_Map(map.get(value));
            }
        }
        else {
            System.out.println("FileError : Impossible d'interragire avec ce lieu.");
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> Object_to_Map(Object object) {
        /* Conversion d'un objet en dictionnaire */
        if (object instanceof Map) {
            return (Map<String, Object>) object;
        }
        else {
            return null;
        }
    }

    private static List<String> Map_to_KeyList (Map<String,Object> object) {
        /* Renvoie une liste des clefs d'un dictionnaire */
        List<String> keysList = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            keysList.add(entry.getKey());
        }
        return keysList;
    }

    private static String Object_to_String(Object object) {
        /* Object => String */
        if (object instanceof String) {
            return object.toString();
        }
        return null;
    }

	/* Fonction inutile
	private static List<?> Object_to_List (Object object) {
		// Object => List
		List<?> list = new ArrayList<>();
		if (object instanceof Collection) {
			list = new ArrayList<>((Collection<?>) object);
		}
		return list;
	}
	*/
}


class World {
    Map<String,Object> univers = new HashMap<String,Object>();

    World () {
        this.univers = JSON.importToHashMap("D:/Programs/Java/Projets/OSC/univers.json");
    }
}