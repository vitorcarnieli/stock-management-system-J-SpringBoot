package br.gov.es.conceicaodocastelo.stock.controllers;

import br.gov.es.conceicaodocastelo.stock.controllers.generic.GenericControllerImp;
import br.gov.es.conceicaodocastelo.stock.controllers.interfaces.StockGroupInterface;
import br.gov.es.conceicaodocastelo.stock.dto.ItemRecordDto;
import br.gov.es.conceicaodocastelo.stock.models.Item;
import br.gov.es.conceicaodocastelo.stock.models.StockGroup;
import jakarta.validation.Valid;
import org.hibernate.QueryException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "/stock-group")
public class StockGroupController extends GenericControllerImp<StockGroup> implements StockGroupInterface {

    @PostMapping(value = "/addItem/{idGroup}")
    public ResponseEntity<Object> addItemToStockGroup(@PathVariable(value = "idGroup") Long idGroup,
            @RequestBody @Valid ItemRecordDto itemRecordDto) {
        try {
            Item item = new Item();
            

            BeanUtils.copyProperties(itemRecordDto, item);
            item.setName(item.getName().substring(0, 1).toUpperCase().concat(item.getName().substring(1)));
            ResponseEntity<Object> stockO = this.findById(idGroup);
            if (stockO.getStatusCode().is2xxSuccessful()) {
                StockGroup stockM = (StockGroup) stockO.getBody();

                stockM.addItems(item);

                stockM.getItems().forEach(i -> {
                    if (i.getUnitType() == null || i.getUnitType() == "") {
                        i.setUnitType("Unidade");
                    }
                    if (i.getAmount() == null || i.getAmount() < 0) {
                        i.setAmount(0);
                    }
                });
                return ResponseEntity.status(HttpStatus.OK).body(this.save(stockM));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }

    }

    @PostMapping(value = "/addItems/{idGroup}")
    public ResponseEntity<Object> addItemsToStockGroup(@PathVariable(value = "idGroup") Long idGroup,
            @RequestBody @Valid List<ItemRecordDto> itemRecordDtos) {
        try {
            List<Item> items = new ArrayList<>();
            itemRecordDtos.forEach(dto -> {
                Item item = new Item();
                BeanUtils.copyProperties(dto, item);
                items.add(item);
            });

            ResponseEntity<Object> stockO = this.findById(idGroup);
            if (stockO.getStatusCode().is2xxSuccessful()) {
                StockGroup stockM = (StockGroup) stockO.getBody();

                stockM.addItems(items);

                stockM.getItems().forEach(i -> {
                    if (i.getUnitType() == null || i.getUnitType() == "") {
                        i.setUnitType("Unidade");
                    }
                    if (i.getAmount() == null || i.getAmount() < 0) {
                        i.setAmount(0);
                    }
                });
                return ResponseEntity.status(HttpStatus.OK).body(this.save(stockM));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }

    }


    // GETER'S
    @GetMapping(path = "/find/byName")
    @ResponseBody
    public ResponseEntity<Object> findByName(@RequestParam(value = "name") String name) {
        ResponseEntity<Object> query = this.findByNameS(name);
        if (query.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.OK).body(query.getBody());
        } else {
            throw new QueryException("not found");
        }
    }

    @GetMapping(value = "/getAllItems")
    @ResponseBody
    public ResponseEntity<Object> getAllItemInStockGroup(@RequestParam(value = "idGroup") Long id) {
        try {
            ResponseEntity<Object> query = this.findById(id);
            if (query.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.OK).body(((StockGroup) this.findById(id).getBody()).getItems());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ID NOT FOUND");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ID NOT FOUND");
        }
    }

    @GetMapping(value = "/report")
    @ResponseBody
    public ResponseEntity<List<String>> createReport() {
        try {
            ResponseEntity<List<StockGroup>> query = this.findAll();
            if (query.getStatusCode().is2xxSuccessful()) {
                List<StockGroup> queryList = query.getBody();
                    
                    List<StockGroup> lista = queryList;
                    Collections.sort(lista, (s1, s2) -> s1.getName().compareTo(s2.getName()));

                    Document document = new Document();
                    String url = "/server/public/general_reports/";
                    String fileName = "report" + new Date().getTime() + ".pdf";
                    System.out.println(url + fileName);
                    PdfWriter.getInstance(document, new FileOutputStream(url + fileName));
                    document.open();
                    document.setPageSize(PageSize.A4);
                    Font fontTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                    Paragraph title = new Paragraph("Relatório de estoque", fontTitle);
                    title.setAlignment(Element.ALIGN_CENTER);
                    title.setSpacingAfter(20f);
                    document.add(title);

                    for (StockGroup stock : lista) {
                        Collections.sort(stock.getItems(), (i1, i2) -> i1.getName().compareTo(i2.getName()));

                        PdfPTable table = new PdfPTable(3);
                        table.setWidthPercentage(100);

                        Font fontSubTitleTableProperties = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                        Paragraph name = new Paragraph("Nome do item", fontSubTitleTableProperties);
                        Paragraph unitType = new Paragraph("Tipo de unidade do item", fontSubTitleTableProperties);
                        Paragraph amount = new Paragraph("Quantidade do item", fontSubTitleTableProperties);
                        name.setAlignment(Element.ALIGN_CENTER);
                        unitType.setAlignment(Element.ALIGN_CENTER);
                        amount.setAlignment(Element.ALIGN_CENTER);

                        table.addCell(new PdfPCell(name));
                        table.addCell(new PdfPCell(unitType));
                        table.addCell(new PdfPCell(amount));

                        for (Item item : stock.getItems()) {
                            if(item.getAmount() <= 0) {
                                Font red =  new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);
                                table.addCell(new PdfPCell(new Paragraph(item.getName() + " - INDISPONÍVEL", red)));
                                table.addCell(new PdfPCell(new Paragraph(item.getUnitType() + " - INDISPONÍVEL",red)));
                                table.addCell(new PdfPCell(new Paragraph(item.getAmount().toString() + " - INDISPONÍVEL", red)));

                            } else {
                                table.addCell(new PdfPCell(new Paragraph(item.getName())));
                                table.addCell(new PdfPCell(new Paragraph(item.getUnitType())));
                                table.addCell(new PdfPCell(new Paragraph(item.getAmount().toString())));

                            }

                        }

                        PdfPTable tituloTable = new PdfPTable(3);
                        tituloTable.setWidthPercentage(100);
                        PdfPCell tituloCell = new PdfPCell(new Phrase("Grupo de estoque: " + stock.getName(),
                                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)));
                        tituloCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tituloCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        tituloCell.setPadding(10);
                        tituloCell.setColspan(3);
                        tituloTable.addCell(tituloCell);

                        document.add(tituloTable);
                        document.add(table);

                        document.add(new Paragraph("\n\n\n"));

                    }
                    SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy - EEEE - HH:mm");
                    String data = formatoData.format(new Date());
                    document.add(new Paragraph("\n\nRelatório gerado no dia " + data));
                    document.close();
                    boolean whilecondition = true;
                    while (whilecondition) {
                        File file = new File(url + fileName);
                        if (file.exists()) {
                            whilecondition = false;
                            continue;
                        }
                    }
                    return ResponseEntity.status(HttpStatus.OK).body(List.of(url, fileName));
            } else {
                throw new RuntimeException();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of("ERROR report not created"));
        }
    }

}
