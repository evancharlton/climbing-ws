package com.buldreinfo.jersey.jaxb.pdf;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.transform.TransformerException;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.buldreinfo.jersey.jaxb.helpers.GlobalFunctions;
import com.buldreinfo.jersey.jaxb.jfreechart.GradeDistributionGenerator;
import com.buldreinfo.jersey.jaxb.leafletprint.LeafletPrintGenerator;
import com.buldreinfo.jersey.jaxb.leafletprint.beans.Leaflet;
import com.buldreinfo.jersey.jaxb.leafletprint.beans.Marker;
import com.buldreinfo.jersey.jaxb.leafletprint.beans.Outline;
import com.buldreinfo.jersey.jaxb.metadata.beans.Setup;
import com.buldreinfo.jersey.jaxb.model.Area;
import com.buldreinfo.jersey.jaxb.model.FaAid;
import com.buldreinfo.jersey.jaxb.model.FaUser;
import com.buldreinfo.jersey.jaxb.model.GradeDistribution;
import com.buldreinfo.jersey.jaxb.model.LatLng;
import com.buldreinfo.jersey.jaxb.model.Media;
import com.buldreinfo.jersey.jaxb.model.MediaSvgElement;
import com.buldreinfo.jersey.jaxb.model.Problem;
import com.buldreinfo.jersey.jaxb.model.Problem.Comment;
import com.buldreinfo.jersey.jaxb.model.Problem.Section;
import com.buldreinfo.jersey.jaxb.model.Problem.Tick;
import com.buldreinfo.jersey.jaxb.model.Sector;
import com.buldreinfo.jersey.jaxb.model.Svg;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class PdfGenerator implements AutoCloseable {
	class MyFooter extends PdfPageEventHelper {
		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			Phrase header = new Phrase("\u00A9 buldreinfo.com & brattelinjer.no", FONT_SMALL);
			Phrase footer = new Phrase("Page " + document.getPageNumber(), FONT_SMALL);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
					header,
					(document.right() - document.left()) / 2 + document.leftMargin(),
					document.top() + 10, 0);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
					footer,
					(document.right() - document.left()) / 2 + document.leftMargin(),
					document.bottom() - 10, 0);
		}
	}
	private static Logger logger = LogManager.getLogger();
	private static Font FONT_SMALL = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
	private static Font FONT_H1 = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	private static Font FONT_H2 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private static Font FONT_REGULAR = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
	private static Font FONT_ITALIC = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);
	private static Font FONT_REGULAR_LINK = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLUE);
	private static Font FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
	private final static int IMAGE_STAR_SIZE = 9;
	public static void main(String[] args) throws Exception {
		int areaId = 2440;
		int problemId = 2442;
		String urlBase = "https://buldreinfo.com";
		Path dst = GlobalFunctions.getPathTemp().resolve("test.pdf");
		Files.createDirectories(dst.getParent());
		try (FileOutputStream fos = new FileOutputStream(dst.toFile())) {
			Gson gson = new Gson();
			List<Sector> sectors = new ArrayList<>();
			URL obj = new URL(urlBase + "/com.buldreinfo.jersey.jaxb/v2/areas?id=" + areaId);
			HttpURLConnection con = (HttpURLConnection)obj.openConnection();
			con.setRequestMethod("GET");
			Area area = gson.fromJson(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")), Area.class);
			for (Area.Sector s : area.getSectors()) {
				obj = new URL(urlBase + "/com.buldreinfo.jersey.jaxb/v2/sectors?id=" + s.getId());
				con = (HttpURLConnection)obj.openConnection();
				con.setRequestMethod("GET");
				sectors.add(gson.fromJson(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")), Sector.class));
			}
			obj = new URL(urlBase + "/com.buldreinfo.jersey.jaxb/v2/problems?id=" + problemId);
			con = (HttpURLConnection)obj.openConnection();
			con.setRequestMethod("GET");
			Problem problem = gson.fromJson(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")), Problem.class);
			try (PdfGenerator generator = new PdfGenerator(fos)) {
				obj = new URL(urlBase + "/com.buldreinfo.jersey.jaxb/v2/grade/distribution?idArea=" + area.getId() + "&idSector=0");
				con = (HttpURLConnection)obj.openConnection();
				con.setRequestMethod("GET");
				List<GradeDistribution> gradeDistribution = gson.fromJson(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")), new TypeToken<ArrayList<GradeDistribution>>(){}.getType());
				generator.writeArea(area, gradeDistribution, sectors);
				//generator.writeProblem(area, sectors.stream().filter(x -> x.getId() == problem.getSectorId()).findAny().get(), problem);
			}
		}
	}
	private final PdfWriter writer;
	private final Document document;
	private final Set<Integer> mediaIdProcessed = Sets.newHashSet();
	private Image imageStarFilled;
	private Image imageStarHalf;
	private Image imageStarEmpty;
	public PdfGenerator(OutputStream output) throws DocumentException, IOException, TranscoderException, TransformerException {
		this.document = new Document();
		this.writer = PdfWriter.getInstance(document, output);
		writer.setStrictImageSequence(true);
		writer.setPageEvent(new MyFooter());
		document.open();
	}

	@Override
	public void close() throws Exception {
		document.close();		
	}

	public void writeArea(Area area, Collection<GradeDistribution> gradeDistribution, List<Sector> sectors) throws DocumentException, IOException, TranscoderException, TransformerException {
		Preconditions.checkArgument(area != null && !sectors.isEmpty());
		String title = area.getName();
		addMetaData(title);
		document.add(new Paragraph(title, FONT_H1));
		writeMapArea(area, sectors);
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Chunk("URL: ", FONT_BOLD));
		Anchor anchor = new Anchor(area.getCanonical(), FONT_REGULAR_LINK);
		anchor.setReference(area.getCanonical());
		paragraph.add(anchor);
		document.add(paragraph);
		paragraph = new Paragraph();
		paragraph.add(new Chunk("PDF generated: ", FONT_BOLD));
		paragraph.add(new Chunk(String.valueOf(LocalDateTime.now()), FONT_REGULAR));
		document.add(paragraph);
		if (!Strings.isNullOrEmpty(area.getComment())) {
			writeHtml(area.getComment());
		}
		if ( (gradeDistribution != null && !gradeDistribution.isEmpty()) || (area.getMedia() != null && !area.getMedia().isEmpty()) ) {
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100);
			if (gradeDistribution != null && !gradeDistribution.isEmpty()) {
				Path png = GlobalFunctions.getPathTemp().resolve("gradeDistribution").resolve(System.currentTimeMillis() + "_" + UUID.randomUUID() + ".png");
				Files.createDirectories(png.getParent());
				GradeDistributionGenerator.write(png, gradeDistribution);
				Image img = Image.getInstance(png.toString());
				PdfPCell cell = new PdfPCell(img, true);
				table.addCell(cell);
			}
			if (area.getMedia() != null && !area.getMedia().isEmpty()) {
				for (Media m : area.getMedia()) {
					writeMediaCell(table, m.getId(), m.getWidth(), m.getHeight(), m.getMediaMetadata().getDescription(), m.getMediaSvgs(), m.getSvgs());
				}
			}
			document.add(table);
		}
		writeSectors(sectors);
	}
	
	public void writeProblem(Area area, Sector sector, Problem problem) throws DocumentException, IOException, TranscoderException, TransformerException {
		Preconditions.checkArgument(area != null && sector != null && problem != null);
		String title = String.format("%s (%s / %s)", problem.getName(), area.getName(), sector.getName());
		addMetaData(title);
		document.add(new Paragraph(title, FONT_H1));
		writeMapProblem(area, sector, problem);
		String html = Joiner.on("<hr/>").skipNulls().join(Lists.newArrayList(area.getComment(), sector.getComment()));
		if (!Strings.isNullOrEmpty(html)) {
			writeHtml(html);
		}

		// Route/Problem info
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Chunk("URL: ", FONT_BOLD));
		Anchor anchor = new Anchor(problem.getCanonical(), FONT_REGULAR_LINK);
		anchor.setReference(problem.getCanonical());
		paragraph.add(anchor);
		document.add(paragraph);
		paragraph = new Paragraph();
		paragraph.add(new Chunk("PDF generated: ", FONT_BOLD));
		paragraph.add(new Chunk(String.valueOf(LocalDateTime.now()), FONT_REGULAR));
		document.add(paragraph);
		paragraph = new Paragraph();
		paragraph.add(new Chunk("Nr: ", FONT_BOLD));
		paragraph.add(new Chunk(String.valueOf(problem.getNr()), FONT_REGULAR));
		document.add(paragraph);
		paragraph = new Paragraph();
		paragraph.add(new Chunk("Type: ", FONT_BOLD));
		String type = problem.getT().getSubType() != null? problem.getT().getType() + " - " + problem.getT().getSubType() : problem.getT().getType();
		paragraph.add(new Chunk(type, FONT_REGULAR));
		document.add(paragraph);
		paragraph = new Paragraph();
		paragraph.add(new Chunk("Grade: ", FONT_BOLD));
		paragraph.add(new Chunk(problem.getGrade(), FONT_REGULAR));
		document.add(paragraph);
		if (problem.getFaAid() != null) {
			FaAid faAid = problem.getFaAid();
			paragraph = new Paragraph();
			paragraph.add(new Chunk("First ascent (Aid): ", FONT_BOLD));
			String faUsers = faAid.getUsers() == null || faAid.getUsers().isEmpty()? null : faAid.getUsers().stream().map(FaUser::getName).collect(Collectors.joining(", "));
			if (!Strings.isNullOrEmpty(faUsers) && !Strings.isNullOrEmpty(faAid.getDateHr())) {
				paragraph.add(new Chunk(faUsers + " (" + faAid.getDateHr() + "). ", FONT_REGULAR));
			}
			else if (!Strings.isNullOrEmpty(faUsers)) {
				paragraph.add(new Chunk(faUsers + ". ", FONT_REGULAR));
			}
			else if (!Strings.isNullOrEmpty(faAid.getDateHr())) {
				paragraph.add(new Chunk(faAid.getDateHr() + ". ", FONT_REGULAR));
			}
			if (!Strings.isNullOrEmpty(faAid.getDescription())) {
				paragraph.add(new Chunk(faAid.getDescription(), FONT_ITALIC));
			}
			document.add(paragraph);
		}
		paragraph = new Paragraph();
		paragraph.add(new Chunk(problem.getFaAid() != null? "First free ascent (FFA): ": "First ascent: ", FONT_BOLD));
		String faUsers = problem.getFa() == null || problem.getFa().isEmpty()? null : problem.getFa().stream().map(FaUser::getName).collect(Collectors.joining(", "));
		if (!Strings.isNullOrEmpty(faUsers) && !Strings.isNullOrEmpty(problem.getFaDateHr())) {
			paragraph.add(new Chunk(faUsers + " (" + problem.getFaDateHr() + "). ", FONT_REGULAR));
		}
		else if (!Strings.isNullOrEmpty(faUsers)) {
			paragraph.add(new Chunk(faUsers + ". ", FONT_REGULAR));
		}
		else if (!Strings.isNullOrEmpty(problem.getFaDateHr())) {
			paragraph.add(new Chunk(problem.getFaDateHr() + ". ", FONT_REGULAR));
		}
		if (!Strings.isNullOrEmpty(problem.getComment())) {
			paragraph.add(new Chunk(problem.getComment(), FONT_ITALIC));
		}
		document.add(paragraph);


		// Pitches
		if (problem.getSections() != null && !problem.getSections().isEmpty()) {
			document.add(new Paragraph(" "));
			PdfPTable table = new PdfPTable(new float[] {1, 1, 10});
			table.setWidthPercentage(100);
			addTableCell(table, FONT_BOLD, "#");
			addTableCell(table, FONT_BOLD, "Grade");
			addTableCell(table, FONT_BOLD, "Description");
			for (Section section : problem.getSections()) {
				addTableCell(table, FONT_REGULAR, String.valueOf(section.getNr()));
				addTableCell(table, FONT_REGULAR, section.getGrade());
				addTableCell(table, FONT_REGULAR, section.getDescription());
			}
			document.add(table);
		}

		// Public ascents
		if (problem.getTicks() != null && !problem.getTicks().isEmpty()) {
			document.add(new Paragraph(" "));
			PdfPTable table = new PdfPTable(new float[] {1, 1, 1, 3});
			table.setWidthPercentage(100);
			addTableCell(table, FONT_BOLD, "Date");
			addTableCell(table, FONT_BOLD, "Grade");
			addTableCell(table, FONT_BOLD, "Name");
			addTableCell(table, FONT_BOLD, "Comment");
			for (Tick tick : problem.getTicks()) {
				addTableCell(table, FONT_REGULAR, tick.getDate());
				Phrase grade = new Phrase(tick.getSuggestedGrade(), FONT_REGULAR);
				appendStarIcons(grade, tick.getStars());
				table.addCell(new PdfPCell(grade));
				addTableCell(table, FONT_REGULAR, tick.getName());
				addTableCell(table, FONT_REGULAR, tick.getComment());
			}
			document.add(table);
		}

		// Comments
		if (problem.getComments() != null && !problem.getComments().isEmpty()) {
			document.add(new Paragraph(" "));
			PdfPTable table = new PdfPTable(new float[] {1, 1, 4});
			table.setWidthPercentage(100);
			addTableCell(table, FONT_BOLD, "When");
			addTableCell(table, FONT_BOLD, "Name");
			addTableCell(table, FONT_BOLD, "Message");
			for (Comment comment : problem.getComments()) {
				addTableCell(table, FONT_REGULAR, comment.getDate());
				addTableCell(table, FONT_REGULAR, comment.getName());
				String url = isValidUrl(comment.getMessage())? comment.getMessage() : null;
				addTableCell(table, url != null? FONT_REGULAR_LINK : FONT_REGULAR, comment.getMessage(), url, false);
			}
			document.add(table);
		}

		// Media
		List<Media> media = Lists.newArrayList();
		if (area.getMedia() != null) {
			media.addAll(area.getMedia());
		}
		if (problem.getMedia() != null) {
			media.addAll(problem.getMedia());
		}
		if (problem.getSections() != null) {
			for (Section s : problem.getSections()) {
				if (s.getMedia() != null) {
					media.addAll(s.getMedia());
				}
			}
		}
		if (!media.isEmpty()) {
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100);
			for (Media m : media) {
				List<Svg> svgs = m.getSvgs() == null? null : m.getSvgs().stream().filter(x -> x.getProblemId() == problem.getId()).collect(Collectors.toList());
				String txt = m.getPitch() > 0? "Pitch " + m.getPitch() : null;
				if (!Strings.isNullOrEmpty(m.getMediaMetadata().getDescription())) {
					if (txt != null) {
						txt += " - " + m.getMediaMetadata().getDescription();
					}
					else {
						txt = m.getMediaMetadata().getDescription();
					}
				}
				writeMediaCell(table, m.getId(), m.getWidth(), m.getHeight(), txt, m.getMediaSvgs(), svgs);
			}
			document.add(table);
		}
	}

	private void addMetaData(String title) {
		document.addTitle(title);
		document.addSubject(title);
		document.addKeywords(title);
		document.addAuthor("Jostein �ygarden (buldreinfo.com / brattelinjer.no");
		document.addCreator("Jostein �ygarden (buldreinfo.com / brattelinjer.no");
	}

	private void addTableCell(PdfPTable table, Font font, String str) {
		addTableCell(table, font, str, null, false);
	}

	private void addTableCell(PdfPTable table, Font font, String str, String url, boolean greenBackground) {
		PdfPCell cell = new PdfPCell(new Phrase(str, font));
		if (url != null) {
			cell.setCellEvent(new LinkInCell(url));
		}
		if (greenBackground) {
			cell.setBackgroundColor(BaseColor.GREEN);
		}
		table.addCell(cell);
	} 

	private void appendStarIcons(Phrase phrase, double stars) throws BadElementException, MalformedURLException, IOException {
		if (stars == 0) {
			phrase.add(new Chunk(getImageStarEmpty(), 0, 0));
			phrase.add(new Chunk(getImageStarEmpty(), 0, 0));
			phrase.add(new Chunk(getImageStarEmpty(), 0, 0));
		}
		else if (stars == 0.5) {
			phrase.add(new Chunk(getImageStarHalf(), 0, 0));
			phrase.add(new Chunk(getImageStarEmpty(), 0, 0));
			phrase.add(new Chunk(getImageStarEmpty(), 0, 0));
		}
		else if (stars == 1) {
			phrase.add(new Chunk(getImageStarFilled(), 0, 0));
			phrase.add(new Chunk(getImageStarEmpty(), 0, 0));
			phrase.add(new Chunk(getImageStarEmpty(), 0, 0));
		}
		else if (stars == 1.5) {
			phrase.add(new Chunk(getImageStarFilled(), 0, 0));
			phrase.add(new Chunk(getImageStarHalf(), 0, 0));
			phrase.add(new Chunk(getImageStarEmpty(), 0, 0));
		}
		else if (stars == 2) {
			phrase.add(new Chunk(getImageStarFilled(), 0, 0));
			phrase.add(new Chunk(getImageStarFilled(), 0, 0));
			phrase.add(new Chunk(getImageStarEmpty(), 0, 0));
		}
		else if (stars == 2.5) {
			phrase.add(new Chunk(getImageStarFilled(), 0, 0));
			phrase.add(new Chunk(getImageStarFilled(), 0, 0));
			phrase.add(new Chunk(getImageStarHalf(), 0, 0));
		}
		else if (stars == 3) {
			phrase.add(new Chunk(getImageStarFilled(), 0, 0));
			phrase.add(new Chunk(getImageStarFilled(), 0, 0));
			phrase.add(new Chunk(getImageStarFilled(), 0, 0));
		}
	}

	private Image getImageStarEmpty() throws BadElementException, MalformedURLException, IOException {
		if (imageStarEmpty == null) {
			imageStarEmpty = Image.getInstance(PdfGenerator.class.getResource("star.png"));
			imageStarEmpty.scaleAbsolute(IMAGE_STAR_SIZE, IMAGE_STAR_SIZE);
		}
		return imageStarEmpty;
	}

	private Image getImageStarFilled() throws BadElementException, MalformedURLException, IOException {
		if (imageStarFilled == null) {
			imageStarFilled = Image.getInstance(PdfGenerator.class.getResource("filled-star.png"));
			imageStarFilled.scaleAbsolute(IMAGE_STAR_SIZE, IMAGE_STAR_SIZE);
		}
		return imageStarFilled;
	}

	private Image getImageStarHalf() throws BadElementException, MalformedURLException, IOException {
		if (imageStarHalf == null) {
			imageStarHalf = Image.getInstance(PdfGenerator.class.getResource("star-half-empty.png"));
			imageStarHalf.scaleAbsolute(IMAGE_STAR_SIZE, IMAGE_STAR_SIZE);
		}
		return imageStarHalf;
	}

	private boolean isValidUrl(String url)  {
		/* Try creating a valid URL */
		try { 
			new URL(url).toURI(); 
			return true; 
		} catch (Exception e) { 
			return false; 
		} 
	}

	private void writeHtml(String html) throws DocumentException, IOException, TranscoderException, TransformerException {
		try (InputStream is = new ByteArrayInputStream(("<p style=\"font-size:12px;\">"+html+"</p>").getBytes())) {
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
		}
	}

	private void writeMapArea(Area area, List<Sector> sectors) {
		try {
			List<Marker> markers = new ArrayList<>();
			List<Outline> outlines = new ArrayList<>();
			List<String> polylines = new ArrayList<>();
			LatLng defaultCenter = null;
			if (area.getLat() > 0 && area.getLng() > 0) {
				defaultCenter = new LatLng(area.getLat(), area.getLng());
			}
			else {
				defaultCenter = area.getMetadata().getDefaultCenter();
			}
			int defaultZoom = 14;

			boolean useLegend = sectors.size()>1;
			List<String> legends = new ArrayList<>();
			for (Sector sector : sectors) {
				if (sector.getLat() > 0 && sector.getLng() > 0) {
					markers.add(new Marker(sector.getLat(), sector.getLng(), Marker.ICON_TYPE.PARKING, null));
				}
				String distance = null;
				if (!Strings.isNullOrEmpty(sector.getPolyline())) {
					polylines.add(sector.getPolyline());
					distance = LeafletPrintGenerator.getDistance(sector.getPolyline());
				}
				if (!Strings.isNullOrEmpty(sector.getPolygonCoords())) {
					final String name = removeIllegalChars(sector.getName()) + (!Strings.isNullOrEmpty(distance)? " (" + distance + ")" : "");
					String label = null;
					if (useLegend) {
						label = String.valueOf(legends.size() + 1);
						legends.add(label + ": " + name);
					}
					else {
						label = name;
					}
					outlines.add(new Outline(label, sector.getPolygonCoords()));
				}
			}

			if (!markers.isEmpty() || !outlines.isEmpty() || !polylines.isEmpty() || defaultCenter != area.getMetadata().getDefaultCenter()) {
				Leaflet leaflet = new Leaflet(markers, outlines, polylines, legends, defaultCenter, defaultZoom, false);
				Path png = LeafletPrintGenerator.takeSnapshot(leaflet);
				if (png != null) {
					PdfPTable table = new PdfPTable(1);
					table.setWidthPercentage(100);
					Image img = Image.getInstance(png.toString());
					PdfPCell cell = new PdfPCell(img, true);
					cell.setColspan(table.getNumberOfColumns());
					table.addCell(cell);
					document.add(new Paragraph(" "));
					document.add(table);
				}
			}
		} catch (Exception | Error e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
	private void writeMapSector(Sector sector) {
		try {
			List<Outline> outlines = new ArrayList<>();
			List<String> polylines = new ArrayList<>();
			List<Marker> markers = new ArrayList<>();
			LatLng defaultCenter = null;
			if (sector.getLat() > 0 && sector.getLng() > 0) {
				defaultCenter = new LatLng(sector.getLat(), sector.getLng());
			}
			else {
				defaultCenter = sector.getMetadata().getDefaultCenter();
			}
			int defaultZoom = 14;
			List<String> legends = new ArrayList<>();
			
			Multimap<String, Sector.Problem> problemsWithCoordinatesGroupedByRock = ArrayListMultimap.create();
			List<Sector.Problem> problemsWithoutRock = new ArrayList<>();
			for (Sector.Problem p : sector.getProblems()) {
				if (p.getLat() > 0 && p.getLng() > 0) {
					if (p.getRock() != null) {
						problemsWithCoordinatesGroupedByRock.put(p.getRock(), p);
					}
					else {
						problemsWithoutRock.add(p);
					}
				}
			}
			for (String rock : problemsWithCoordinatesGroupedByRock.keySet()) {
				Collection<Sector.Problem> problems = problemsWithCoordinatesGroupedByRock.get(rock);
				LatLng latLng = LeafletPrintGenerator.getCenter(problems);
				markers.add(new Marker(latLng.getLat(), latLng.getLng(), Marker.ICON_TYPE.ROCK, rock));
			}
			for (Sector.Problem p : problemsWithoutRock) {
				markers.add(new Marker(p.getLat(), p.getLng(), Marker.ICON_TYPE.DEFAULT, String.valueOf(p.getNr())));
			}
			if (markers.size() >= 1 && markers.size() <= 8) {
				if (sector.getLat() > 0 && sector.getLng() > 0) {
					markers.add(new Marker(sector.getLat(), sector.getLng(), Marker.ICON_TYPE.PARKING, null));
				}
				String distance = null;
				if (!Strings.isNullOrEmpty(sector.getPolyline())) {
					polylines.add(sector.getPolyline());
					distance = LeafletPrintGenerator.getDistance(sector.getPolyline());
				}
				if (!Strings.isNullOrEmpty(sector.getPolygonCoords())) {
					final String label = removeIllegalChars(sector.getName()) + (!Strings.isNullOrEmpty(distance)? " (" + distance + ")" : "");
					outlines.add(new Outline(label, sector.getPolygonCoords()));
				}
			}

			if (!markers.isEmpty()) {
				Leaflet leaflet = new Leaflet(markers, outlines, polylines, legends, defaultCenter, defaultZoom, true);
				Path png = LeafletPrintGenerator.takeSnapshot(leaflet);
				if (png != null) {
					PdfPTable table = new PdfPTable(1);
					table.setWidthPercentage(100);
					Image img = Image.getInstance(png.toString());
					PdfPCell cell = new PdfPCell(img, true);
					cell.setColspan(table.getNumberOfColumns());
					table.addCell(cell);
					document.add(new Paragraph(" "));
					document.add(table);
				}
			}
		} catch (Exception | Error e) {
			logger.warn(e.getMessage(), e);
		}
	}

	private void writeMapProblem(Area area, Sector sector, Problem problem) {
		try {
			List<Marker> markers = new ArrayList<>();
			List<Outline> outlines = new ArrayList<>();
			List<String> polylines = new ArrayList<>();
			LatLng defaultCenter = null;
			if (problem.getLat() > 0 && problem.getLng() > 0) {
				defaultCenter = new LatLng(problem.getLat(), problem.getLng());
			}
			else if (sector.getLat() > 0 && sector.getLng() > 0) {
				defaultCenter = new LatLng(sector.getLat(), sector.getLng());
			}
			else if (area.getLat() > 0 && area.getLng() > 0) {
				defaultCenter = new LatLng(area.getLat(), area.getLng());
			}
			else {
				defaultCenter = area.getMetadata().getDefaultCenter();
			}
			int defaultZoom = 15;

			if (sector.getLat() > 0 && sector.getLng() > 0) {
				markers.add(new Marker(sector.getLat(), sector.getLng(), Marker.ICON_TYPE.PARKING, null));
			}
			if (problem.getLat() > 0 && problem.getLng() > 0) {
				String name = removeIllegalChars(problem.getName());
				markers.add(new Marker(problem.getLat(), problem.getLng(), Marker.ICON_TYPE.DEFAULT, name));
			}
			String distance = null;
			if (!Strings.isNullOrEmpty(sector.getPolyline())) {
				polylines.add(sector.getPolyline());
				distance = LeafletPrintGenerator.getDistance(sector.getPolyline());	
			}
			if (!Strings.isNullOrEmpty(sector.getPolygonCoords())) {
				String label = removeIllegalChars(sector.getName()) + (!Strings.isNullOrEmpty(distance)? " (" + distance + ")" : "");
				outlines.add(new Outline(label, sector.getPolygonCoords()));
			}

			if (!markers.isEmpty() || !outlines.isEmpty() || !polylines.isEmpty() || defaultCenter != area.getMetadata().getDefaultCenter()) {
				Leaflet leaflet = new Leaflet(markers, outlines, polylines, null, defaultCenter, defaultZoom, false);
				Path png = LeafletPrintGenerator.takeSnapshot(leaflet);
				if (png != null) {
					PdfPTable table = new PdfPTable(1);
					table.setWidthPercentage(100);
					Image img = Image.getInstance(png.toString());
					PdfPCell cell = new PdfPCell(img, true);
					cell.setColspan(table.getNumberOfColumns());
					table.addCell(cell);
					
					// Also append photo map
					markers = markers.stream().filter(m -> !m.getIconType().equals(Marker.ICON_TYPE.PARKING)).collect(Collectors.toList());
					if (!markers.isEmpty()) {
						outlines.clear();
						polylines.clear();
						leaflet = new Leaflet(markers, outlines, polylines, null, defaultCenter, defaultZoom, true);
						png = LeafletPrintGenerator.takeSnapshot(leaflet);
						if (png != null) {
							img = Image.getInstance(png.toString());
							cell = new PdfPCell(img, true);
							cell.setColspan(table.getNumberOfColumns());
							table.addCell(cell);
						}
					}
					
					document.add(new Paragraph(" "));
					document.add(table);
				}
			}
		} catch (Exception | Error e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
	private String removeIllegalChars(String name) {
		if (name != null) {
			return name.replaceAll("[^������a-zA-Z0-9]", " ");
		}
		return name;
	}

	private void writeMediaCell(PdfPTable table, int mediaId, int width, int height, String txt, List<MediaSvgElement> mediaSvgs, List<Svg> svgs) throws MalformedURLException, IOException, DocumentException, TranscoderException, TransformerException {
		Image img = null;
		if ((mediaSvgs == null || mediaSvgs.isEmpty()) && (svgs == null || svgs.isEmpty())) {
			if (mediaIdProcessed.add(mediaId)) {
				URL url = new URL(GlobalFunctions.getUrlJpgToImage(mediaId));
				img = Image.getInstance(url);
			}
		}
		else {
			Path topo = TopoGenerator.generateTopo(mediaId, width, height, mediaSvgs, svgs);
			img = Image.getInstance(topo.toString());
		}
		if (img != null) {
			PdfPCell cell = new PdfPCell(img, true);
			cell.setColspan(table.getNumberOfColumns());
			table.addCell(cell);
			if (!Strings.isNullOrEmpty(txt)) {
				cell = new PdfPCell(new Phrase(txt, FONT_ITALIC));
				cell.setColspan(table.getNumberOfColumns());
				table.addCell(cell);
			}
		}
	}

	private void writeSectors(List<Sector> sectors) throws DocumentException, IOException, TranscoderException, TransformerException {
		// TODO Include trivia + ice-fields? trivia, starting_altitude, aspect, route_length, descent
		for (Sector s : sectors) {
			final boolean showType = s.getMetadata().getGradeSystem().equals(Setup.GRADE_SYSTEM.CLIMBING);
			document.newPage();
			new PdfOutline(writer.getRootOutline(), new PdfDestination(PdfDestination.FITH, writer.getVerticalPosition(true)), s.getName(), true);
			document.add(new Paragraph(s.getName(), FONT_H2));
			if (!Strings.isNullOrEmpty(s.getComment())) {
				document.add(new Phrase(s.getComment(), FONT_REGULAR));
			}
			writeMapSector(s);
			// Table
			float[] relativeWidths = showType? new float[]{1, 4, 2, 2, 4, 7} : new float[]{1, 3, 1, 3, 8};
			PdfPTable table = new PdfPTable(relativeWidths);
			table.setWidthPercentage(100);
			addTableCell(table, FONT_BOLD, "#");
			addTableCell(table, FONT_BOLD, "Name");
			addTableCell(table, FONT_BOLD, "Grade");
			if (showType) {
				addTableCell(table, FONT_BOLD, "Type");
			}
			addTableCell(table, FONT_BOLD, "FA");
			addTableCell(table, FONT_BOLD, "Note");
			for (Sector.Problem p : s.getProblems()) {
				String description = Strings.emptyToNull(p.getComment());
				if (!Strings.isNullOrEmpty(p.getRock())) {
					if (description == null) {
						description = "Rock: " + p.getRock();
					}
					else {
						description = "Rock: " + p.getRock() + ". " + description;
					}
				}
				addTableCell(table, FONT_REGULAR, String.valueOf(p.getNr()), null, p.isTicked());
				String url = s.getMetadata().getCanonical();
				url = url.substring(0, url.indexOf("/sector"));
				url += "/problem/" + p.getId();
				addTableCell(table, FONT_REGULAR_LINK, p.getName(), url, p.isTicked());
				addTableCell(table, FONT_REGULAR, p.getGrade(), null, p.isTicked());
				if (showType) {
					addTableCell(table, FONT_REGULAR, p.getT().getSubType(), null, p.isTicked());
				}
				addTableCell(table, FONT_REGULAR, p.getFa(), null, p.isTicked());
				Phrase note = new Phrase();
				if (p.getNumTicks() > 0) {
					appendStarIcons(note, p.getStars());
					note.add(new Chunk(" " + p.getNumTicks() + " ascent" + (p.getNumTicks()==1? "" : "s"), FONT_REGULAR));
				}
				if (description != null) {
					note.add(new Chunk((p.getNumTicks() > 0? " - " : "") + description, FONT_ITALIC));
				}
				PdfPCell cell = new PdfPCell(note);
				if (p.isTicked()) {
					cell.setBackgroundColor(BaseColor.GREEN);
				}
				table.addCell(cell);
			}
			if (s.getMedia() != null) {
				for (Media m : s.getMedia()) {
					writeMediaCell(table, m.getId(), m.getWidth(), m.getHeight(), m.getMediaMetadata().getDescription(), m.getMediaSvgs(), m.getSvgs());
				}
			}
			document.add(new Paragraph(" "));
			document.add(table);
		}
	}
}