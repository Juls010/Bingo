package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.FileOutputStream;

public class Card {

	private final Integer[][] matrix = new Integer[3][9];

	public Card() {
		generateMatrix();
	}

	private void generateMatrix() {

		List<List<Integer>> columns = generateColumns();

		fillMatrixWithNumbers(columns);

		createEmptySpaces();
	}

	private List<List<Integer>> generateColumns() {
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

		return columns;
	}

	private void fillMatrixWithNumbers(List<List<Integer>> columns) {

		for (int i = 0; i < 3; i++) {
			Arrays.fill(matrix[i], null);
		}

		for (int col = 0; col < 9; col++) {
			List<Integer> numbers = columns.get(col);
			for (int row = 0; row < 3; row++) {
				matrix[row][col] = numbers.get(row);
			}
		}
	}

	private void createEmptySpaces() {
		for (int row = 0; row < 3; row++) {
			List<Integer> indices = IntStream.range(0, 9).boxed().collect(Collectors.toList());
			Collections.shuffle(indices);

			for (int j = 0; j < 4; j++) {
				matrix[row][indices.get(j)] = null;
			}
		}
	}

	public void exportToPdf(String filePath) {
		List<Card> singleCard = Arrays.asList(this);
		exportMultipleCardsToPdf(singleCard, filePath);
	}

	public static List<Card> generateMultipleCards(int count) {
		List<Card> cards = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			cards.add(new Card());
		}
		return cards;
	}

	public static void exportMultipleCardsToPdf(List<Card> cards, String filePath) {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(filePath));
			document.open();

			for (int i = 0; i < cards.size(); i++) {
				PdfPTable headerTable = createBingoHeaderPinkStyle();
				headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);
				document.add(headerTable);

				PdfPTable table = createBingoTable(cards.get(i));
				table.setHorizontalAlignment(Element.ALIGN_CENTER);
				document.add(table);

				if (i < cards.size() - 1) {
					document.add(Chunk.NEWLINE);
					document.add(Chunk.NEWLINE);
					document.add(new Paragraph("════════════════════════════════════════════════════════════"));
					document.add(Chunk.NEWLINE);
				}
			}

			Font footerFont = loadCustomFont();
			footerFont.setSize(10);
			footerFont.setColor(150, 150, 150);
			Paragraph footer = new Paragraph("¡BUENA SUERTE! 🍀", footerFont);
			footer.setAlignment(Element.ALIGN_CENTER);
			footer.setSpacingBefore(30f);
			document.add(footer);

			document.close();

		} catch (Exception e) {
			System.err.println("Error generando PDF: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static PdfPTable createBingoTable(Card card) {
		PdfPTable table = new PdfPTable(9);
		table.setWidthPercentage(85);
		table.setSpacingBefore(15f);
		table.setSpacingAfter(15f);

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				Integer number = card.getMatrix()[row][col];
				String content = number == null ? "" : String.valueOf(number);

				Font cellFont = loadCustomFont();
				cellFont.setSize(13);
				cellFont.setColor(new Color(60, 60, 60));
				PdfPCell cell = new PdfPCell(new Phrase(content, cellFont));

				if (!content.isEmpty()) {
					cell.setBackgroundColor(Color.WHITE);
					cell.setBorderWidth(3f);
					cell.setBorderColor(new Color(255, 220, 80));
				} else {
					Color bgColor;
					int colorIndex = (row + col) % 4;
					switch (colorIndex) {
					case 0:
						bgColor = new Color(255, 182, 193);
						break; 
					case 1:
						bgColor = new Color(255, 200, 190);
						break; 
					case 2:
						bgColor = new Color(255, 215, 180);
						break; 
					default:
						bgColor = new Color(255, 190, 100);
						break; 
					}
					cell.setBackgroundColor(bgColor);
					cell.setBorderWidth(1.5f);
					cell.setBorderColor(new Color(255, 140, 160));
				}

				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setFixedHeight(32f);
				cell.setPadding(6f);

				table.addCell(cell);
			}
		}

		return table;
	}

	private static PdfPTable createBingoHeaderPinkStyle() {
		PdfPTable headerTable = new PdfPTable(5);
		headerTable.setWidthPercentage(60);
		headerTable.setSpacingAfter(10f);

		String[] letters = { "B", "O", "N", "G", "I" };
		Color[] colors = { new Color(255, 105, 180), 
				new Color(255, 128, 160), 
				new Color(255, 153, 130), 
				new Color(255, 190, 100), 
				new Color(255, 220, 80) 
		};

		for (int i = 0; i < letters.length; i++) {
			Font letterFont = loadCustomFont();
			letterFont.setSize(28);
			letterFont.setColor(Color.WHITE);

			PdfPCell cell = new PdfPCell(new Phrase(letters[i], letterFont));
			cell.setBackgroundColor(colors[i]);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(45f);
			cell.setBorderWidth(3f);
			cell.setBorderColor(Color.WHITE);
			cell.setPadding(8f);
			headerTable.addCell(cell);
		}

		return headerTable;
	}

	public static void generateAndExportCards(int count, String filePath) {
		List<Card> cards = generateMultipleCards(count);
		exportMultipleCardsToPdf(cards, filePath);
	}

	public Integer[][] getMatrix() {
		return matrix;
	}

	private static Font loadCustomFont() {
		try {
			String fontPath = "/Fonts/Coiny-Regular.ttf";
			BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			return new Font(baseFont, 14, Font.NORMAL);
		} catch (Exception e) {
			System.err.println("Error cargando fuente personalizada: " + e.getMessage());
			return FontFactory.getFont(FontFactory.HELVETICA, 14);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bongi-Cartones:\n");
		for (Integer[] row : matrix) {
			for (Integer number : row) {
				sb.append(String.format("%3s ", number == null ? "  " : number));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}
