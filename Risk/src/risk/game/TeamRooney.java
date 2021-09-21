//Philip Byrne, James Mallen, Feras Haddad
package risk.game;

// put your code here
import java.util.ArrayList;
import java.util.Comparator;

public class TeamRooney implements Bot {
    // The public API of YourTeamName must not change
    // You cannot change any other classes
    // YourTeamName may not alter the state of the board or the player objects
    // It may only inspect the state of the board and the player objects
    // So you can use player.getNumUnits() but you can't use player.addUnits(10000), for example

    private BoardAPI board;
    private PlayerAPI player;

    TeamRooney (BoardAPI inBoard, PlayerAPI inPlayer) {
        board = inBoard;
        player = inPlayer;
    }

    public String getName () {
        String command;
        command = "TeamRooneyBot";
        return(command);
    }

    public ArrayList<Countries>borderedCountries = new ArrayList<>();
    public String getReinforcement () {
        String country;
        String command;
        getBorderedCountries(0);
        if (borderedCountries.size() == 0) {
            getBorderedCountries(-1);
        }
        borderedCountries.sort(compareUnits);
        country = borderedCountries.get(0).name;
        country = country.replaceAll("\\s", "");
        command = country + " 1";
        return(command);
    }

    Comparator<Countries> compareUnits = Comparator.comparingInt(Countries::numUnits);

    private void getBorderedCountries(int index){
        for (int i=0; i<GameData.NUM_COUNTRIES; i++){
            if (board.getOccupier(i) == player.getId()){
                for (int j=0; j<GameData.ADJACENT[i].length; j++){
                    if (index == -1) {
                        if (board.getOccupier(GameData.ADJACENT[i][j]) != player.getId()) {
                            borderedCountries.add(new Countries(i));
                        }
                    } else {
                        if (board.getOccupier(GameData.ADJACENT[i][j]) == getOppositionID(player.getId())) {
                            borderedCountries.add(new Countries(i));
                        }
                    }
                }
            }
        }
    }

    public String getPlacement (int forPlayer) {
        String command;
        int size;
        int index;
        ArrayList<Countries> countries = new ArrayList<>();
        for(int i=0; i<GameData.COUNTRY_NAMES.length; i++){
            if(board.getOccupier(i) == forPlayer){
                countries.add(new Countries(i));
            }
        }
        size = countries.size();
        index = (int)(Math.random() * size);
        command = countries.get(index).name;
        command = command.replaceAll("\\s", "");
        return(command);
    }

    public String getCardExchange () {
        String command;
        int a = 0;
        int c = 0;
        int i = 0;
        int w = 0;
        for(int j=0; j<player.getCards().size(); j++){
            if(player.getCards().get(j).getInsigniaId() == 0){
                i++;
            }else if(player.getCards().get(j).getInsigniaId() == 1){
                c++;
            }else if(player.getCards().get(j).getInsigniaId() == 2){
                a++;
            }else{
                w++;
            }
        }
        if(player.isForcedExchange()){
            if(a >= 3){
                command = "AAA";
            }else if(c >= 3){
                command = "CCC";
            }else if(i >= 3){
                command = "III";
            }else if(a >= 1 && i >= 1 && c >= 1){
                command = "AIC";
            }else if(w >= 1 && i >= 1 && c >= 1){
                command = "WIC";
            }else if(a >= 1 && w >= 1 && c >= 1){
                command = "AWC";
            }else{
                command = "AIW";
            }
        }else{
            if(a >= 3){
                command = "AAA";
            }else if(c >= 3){
                command = "CCC";
            }else if(i >= 3){
                command = "III";
            }else if(a >= 1 && i >= 1 && c >= 1){
                command = "AIC";
            }else if(w >= 1 && i >= 1 && c >= 1){
                command = "WIC";
            }else if(a >= 1 && w >= 1 && c >= 1){
                command = "AWC";
            }else if(a >= 1 && i >= 1 && w >= 1){
                command = "AIW";
            }else{
                command = "skip";
            }
        }
        return(command);
    }

    public String getBattle () {
        String command;
        ArrayList<Countries> myCountries = new ArrayList<>();
        ArrayList<Countries> countries = new ArrayList<>();
        ArrayList<Countries> bestCountry = new ArrayList<>();
        ArrayList<Countries> bestAttackCountry = new ArrayList<>();

        for(int i=0; i<GameData.COUNTRY_NAMES.length; i++){
                countries.add(new Countries(i));
        }

        for (int i=0; i<countries.size(); i++){
            if(board.getOccupier(countries.get(i).countryID) == player.getId()){
                myCountries.add(countries.get(i));
            }
        }

        for(int i=0; i<countries.size(); i++){
            for(int j=0; j<myCountries.size(); j++) {
                if (board.isAdjacent(countries.get(i).countryID, myCountries.get(j).countryID)) {
                    bestAttackCountry.add(countries.get(i));
                }
            }
        }

        for(int i=0; i<bestAttackCountry.size(); i++){
            if(board.getOccupier(bestAttackCountry.get(i).countryID) == player.getId()){
                bestAttackCountry.remove(i);
            }
        }

        for(int i=0; i<bestAttackCountry.size(); i++){
            for(int j=0; j<countries.size(); j++) {
                if (board.isAdjacent(bestAttackCountry.get(i).countryID, countries.get(j).countryID)) {
                    bestCountry.add(countries.get(j));
                }
            }
        }

        for(int i=0; i<bestCountry.size(); i++){
            if(board.getOccupier(bestCountry.get(i).countryID) != player.getId()){
                bestCountry.remove(i);
            }
        }

         String myCountry = "";
         String attackCountry = "";

            for(int i = 0; i<bestAttackCountry.size(); i++){
                for(int j=0; j< bestCountry.size(); j++){
                    if(board.isAdjacent(bestAttackCountry.get(i).countryID, bestCountry.get(j).countryID) && bestCountry.get(j).numUnits() > bestAttackCountry.get(i).numUnits() && board.getOccupier(bestCountry.get(j).countryID) != board.getOccupier(bestAttackCountry.get(i).countryID) && board.getOccupier(bestCountry.get(j).countryID) == player.getId()){
                        myCountry = bestCountry.get(j).name;
                        attackCountry = bestAttackCountry.get(i).name;
                    }
                }
            }

        if(myCountry == "" || attackCountry == "") {
            command = "skip";
        } else {
            myAttackingCountry = myCountry;
            command = myCountry.replaceAll("\\s", "") + " " + attackCountry.replaceAll("\\s", "") + " " + 1;
        }
        return(command);
    }


    public String getDefence (int countryId) {
        String command;
        if (board.getNumUnits(countryId) >= 2){
            command = "2";
        } else {
            command = "1";
        }
        return(command);
    }

    public String myAttackingCountry = "";

    public String getMoveIn (int attackCountryId) {
        String command = "";
        int id = 0;
        for(int i= 0; i<GameData.COUNTRY_NAMES.length; i++){
            if(myAttackingCountry == GameData.COUNTRY_NAMES[i]){
                id = i;
            }
        }
        int numUnitsMoved = board.getNumUnits(id) - 1;
        command = "" + numUnitsMoved;
        return(command);
    }
    
    public String getFortify(){
        String command = "skip";
        ArrayList<Countries> country = new ArrayList<>();
        for(int i=0; i<GameData.COUNTRY_NAMES.length; i++){
            if(board.getOccupier(i) == player.getId()){
                country.add(new Countries(i));
            }
        }
        country.sort(compareUnits);
        for(int i=0; i<country.size();i++){
            for(int j = 0; j<country.size(); j++){
                if(board.isAdjacent(country.get(i).countryID, country.get(j).countryID) && country.get(j).numUnits() > 1){
                    for(int a=0; a<country.get(j).adjacent.length; a++){
                        if(board.getOccupier(country.get(j).adjacent[a]) == getOppositionID(player.getId())){
                            command = "skip";
                        }else{
                            command = country.get(j).name.replaceAll("\\s", "") + " " + country.get(i).name.replaceAll("\\s", "") + " " + country.get(j).numUnits()/2;
                            break;
                        }
                    }
                }
            }
        }
        return(command);
    }


    class Countries {
        public int countryID;
        public String name;
        public int[] adjacent;
        public int continent;
        public Countries(int countryID) {
            this.countryID = countryID;
            name = GameData.COUNTRY_NAMES[countryID];
            adjacent = GameData.ADJACENT[countryID];
            continent = GameData.CONTINENT_IDS[countryID];
        }
        public int numUnits(){ return board.getNumUnits(countryID);}
    }

    private int getOppositionID(int playerID){
        if(playerID == 0){
            return 1;
        }else {
            return 0;
        }
    }
}




