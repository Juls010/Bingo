package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.FileOutputStream;


public class Card {

	private final Integer[][] matrix = new Integer [3][9];
	
	public Card() {
		
		generateCard();
	}
	
	
	private void generateCard(){
		
		List<List<Integer>> columns = new ArrayList<>();
		
		for (int i = 0; i < 9; i++) {
			int min = i * 10 + 1;
			int max = (i == 8) ? 90 : min + 9;
			
			List<Integer> col = new ArrayList<>();
			for (int j = min; j <= max; j++) {
				col.add(j);
			}
			Collections.shuffle(col);
			columns.add(col.subList(0, 3));
		}
		
		for(int i = 0; i < 3; i++) {
			Arrays.fill(matrix[i], null);
		}
		
		
		
		for (int col = 0; col < 9; col++) {
			List<Integer> number =  columns.get(col);
			for (int i = 0; i < 3; i++) {
				matrix[i][col] = number.get(i);
			}
		}
		
		for (int i = 0; i < 3; i++) {
			List<Integer> index = IntStream.range(0, 9).boxed().collect(Collectors.toList());
			Collections.shuffle(index);
			for (int j = 0; j < 4; j++) {
				matrix[i][index.get(j)] = null;
			}
		}
		
	}
	
	
	public void exportPdf(String route) {
	    try {
	    	Document document = new Document();
	        PdfWriter.getInstance(document, new FileOutputStream(route));
	        document.open();

	        document.add(new Paragraph("BINGO CARD"));
	        document.add(Chunk.NEWLINE);

	        PdfPTable table = new PdfPTable(9); // 9 columnas como en el cartón clásico

	        for (Integer[] row : this.matrix) {
	            for (Integer number : row) {
	                String content = number == null ? "" : String.valueOf(number);
	                PdfPCell cell = new PdfPCell(new Phrase(content));
	                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                cell.setFixedHeight(30f);
	                table.addCell(cell);
	            }
	        }

	        document.add(table);
	        document.close();
	        System.out.println("PDF GENERATE: " + route);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	    
	public static List<Card> generateCard(int count){
		List<Card> cards = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			cards.add(new Card());
		}
		return cards;
	}
	
	
	public Integer [][] getMatrix(){
		
		return matrix;
	}
	
}
