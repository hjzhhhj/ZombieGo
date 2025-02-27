// FileHandler.java
package hjzhhhj;

import java.io.*;
import java.util.*;

public class FileHandler {

    private String filename;

    public FileHandler(String filename) {
        this.filename = filename;
        createFileIfNotExists(); // 파일 없으면 생성
    }

    private void createFileIfNotExists() {
        File file = new File(filename);
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs(); // 디렉토리 생성
            }
            if (!file.exists()) {
                file.createNewFile(); // 파일 생성
                System.out.println("scores.txt 생성됨: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // schoolId에 해당하는 점수를 가져오는 메서드
    public int getScore(String schoolId) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(schoolId)) {
                    return Integer.parseInt(parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("점수 파싱 오류: " + e.getMessage());
        }
        return 0; // 점수가 없거나 오류 발생 시 0 반환
    }

    // schoolId에 해당하는 점수를 업데이트하는 메서드
    public void updateScore(String schoolId, int score) {
        System.out.println("Updating score...");
        Map<String, Integer> scores = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        scores.put(parts[0], Integer.parseInt(parts[1]));
                    } catch (NumberFormatException e) {
                        System.err.println("점수 파싱 오류: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        scores.put(schoolId, Math.max(scores.getOrDefault(schoolId, 0), score));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, false))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 파일이 제대로 저장됐는지 확인
        System.out.println("Saved scores in file:");
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Map.Entry<String, Integer>> getTopScores(int count) {
        Map<String, Integer> scores = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        scores.put(parts[0], Integer.parseInt(parts[1]));
                    } catch (NumberFormatException e) {
                        System.err.println("점수 파싱 오류: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(scores.entrySet());
        sortedScores.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.nullsLast(Comparator.reverseOrder())));
        return sortedScores.subList(0, Math.min(count, sortedScores.size()));
    }

    public void printFilePath() {
        System.out.println("Absolute path of scores.txt: " + new File(filename).getAbsolutePath());
    }

    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler("src/hjzhhhj/scores.txt"); // hjzhhhj 폴더 안에 저장
        fileHandler.printFilePath(); // 파일 경로 확인
        fileHandler.updateScore("12345", 100);
        System.out.println("Score for 12345: " + fileHandler.getScore("12345"));
    }
}
