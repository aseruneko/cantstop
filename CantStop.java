/**
 * ボードゲーム「Can't stop」の確率検証用プログラム
 * @author aseruneko
 * @version 1.0.0
 */

package cantstop;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

public class CantStop {

    static final int[] ROAD_LENGTH = {0,0,3,5,7,9,11,13,11,9,7,5,3};
    static final int TRYING = 10000;
    static int goaled = 0;

    public static void main(String[] args){

        for(int i = 0; i < TRYING; i ++){
            boolean isGoal = false;
            boolean isGameOver = false;
            int round = 0;
            int[] dice;
            int[][] dividedDice;
            int[] chosenDice;
            int[][] chosenRoad = {{-1,0},{-1,0},{-1,0}};

            round ++;
//            System.out.println("[ROUND " + round + "]");
            dice = diceRoll();
            dividedDice = divideDice(dice);
            chosenDice = initialChoice(dividedDice);
            chosenRoad = solveDice(chosenRoad, chosenDice);

//            printDice(chosenDice);
//            printProgress(chosenRoad);

            while(!isGoal && !isGameOver) {
                round ++;
//                System.out.println("[ROUND " + round + "]");
                dice = diceRoll();
                dividedDice = divideDice(dice);
                chosenDice = nonInitialChoice(chosenRoad, dividedDice);
                if(chosenDice == null){
//                    System.out.println("GAMEOVER");
                    isGameOver = true;
                } else {
                    chosenRoad = solveDice(chosenRoad, chosenDice);

//                    printDice(chosenDice);
//                    printProgress(chosenRoad);
                }

                isGoal = isGoal(chosenRoad);
                if(isGoal) goaled++;

            }
        }
        System.out.println(goaled + "/" + TRYING);
        System.out.println((float)goaled / TRYING * 100 + "%");



    }

    private static int[] diceRoll(){
        Random rand = new Random();
        return new int[]{
                rand.nextInt(6) + 1,
                rand.nextInt(6) + 1,
                rand.nextInt(6) + 1,
                rand.nextInt(6) + 1
        };
    }

    private static int[][] divideDice(int[] dice){
        return new int[][] {
                {dice[0] + dice[1], dice[2] + dice[3]},
                {dice[0] + dice[2], dice[1] + dice[3]},
                {dice[0] + dice[3], dice[1] + dice[2]}
        };
    }

    private static int[] initialChoice(int[][] dividedDice){
        return Stream.of(dividedDice)
                .min(Comparator
                    .<int[], Integer>comparing(a -> (Math.abs(a[0] - 7) + Math.abs(a[1] - 7)))
                    .thenComparing( a -> (Math.abs(a[0] - a[1])))
                )
                .orElse(null);
    }

    private static int[][] solveDice(int[][] chosenRoad, int[] chosenDice){
        for(int i = 0; i < 2; i ++){
            for(int j = 0; j < chosenRoad.length; j ++){
                if(chosenRoad[j][0] == chosenDice[i] || chosenRoad[j][0] == -1){
                    chosenRoad[j][0] = chosenDice[i];
                    chosenRoad[j][1] ++;
                    break;
                }
            }
        }
        return chosenRoad;
    }

    private static int[] nonInitialChoice(int[][] chosenRoad, int[][] dividedDice){
        int[] matchRoad = {0,0,0};
        for(int i = 0; i < dividedDice.length; i++){
            for(int[] road : chosenRoad){
                if (road[0] == -1){
                    matchRoad[i]++;
                } else {
                    for(int dice : dividedDice[i]){
                        if (road[0] == dice) matchRoad[i] += 5;
                    }
                }
            }
        }
        if(matchRoad[0] + matchRoad[1] + matchRoad[2] == 0){
            return null;
        }else{
            int maxId = 0;
            for(int i = 0; i < matchRoad.length; i++){
                maxId = matchRoad[i] > matchRoad[maxId] ? i : maxId;
            }
            return dividedDice[maxId];
        }
    }

    private static boolean isGoal(int[][] chosenRoad){
        return Stream.of(chosenRoad)
                .filter(road -> road[0] >= 0)
                .anyMatch(road -> road[1] >= ROAD_LENGTH[road[0]]);
    }

    private static void printProgress(int[][] chosenRoad){
        String output = "";
        for(int[] road : chosenRoad){
            output += road[0] + ":" + road[1] + ",";
        }
        System.out.println(output);
    }

    private static void printDice(int[] chosenDice){
        String output = "";
        for(int dice : chosenDice){
            output += dice + ",";
        }
        System.out.println(output);
    }


}
