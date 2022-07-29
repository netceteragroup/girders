# PDF

Girders supports two technologies for generating and manipulating PDF documents. **PDFBox** is an excellent choice
for the manipulation of existing PDF documents and PDF forms. Manipulation of the PDFs is done through an API.
**Apache FOP**'s strength on the other hand is the generation of PDF documents from abstract templates. This is not
done through an API, but through templating and transformations (using XSL:FO).

## PDFBox

[PDFBox](https://pdfbox.apache.org) is a complete and self-contained library to manipulate PDF documents (quote
from website):

  * Extract Unicode text from PDF files.
  * Split a single PDF into many files or merge multiple PDF files.
  * Extract data from PDF forms or fill a PDF form.
  * Validate PDF files against the PDF/A-1b standard.
  * Print a PDF file using the standard Java printing API.
  * Save PDFs as image files, such as PNG or JPEG.
  * Create a PDF from scratch, with embedded fonts and images.
  * Digitally sign PDF files.

Girders provides a starter dependencies for the integration of PDFBox:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-pdfbox</artifactId>
    </dependency>
    
The starter does not include any auto-configuration, but just provides PDFbox as a dependency into your project.

### Filling PDF forms with PDFBox

A very common use case at NCA is populating PDF forms loading from internal or external sources with runtime data.
PDFBox provides a small example for that:
    
    String formTemplate = "src/main/resources/org/apache/pdfbox/examples/interactive/form/FillFormField.pdf";
        
    // load the document
    PDDocument pdfDocument = PDDocument.load(new File(formTemplate));
      
    // get the document catalog
    PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
      
    // as there might not be an AcroForm entry a null check is necessary
    if (acroForm != null) {
      // Retrieve an individual field and set its value.
      PDTextField field = (PDTextField) acroForm.getField("sampleField");
      field.setValue("Text Entry");
    
      // If a field is nested within the form tree a fully qualified name
      // might be provided to access the field.
      field = (PDTextField) acroForm.getField("fieldsContainer.nestedSampleField");
      field.setValue("Text Entry");
    }
      
    // Save and close the filled out form.
    pdfDocument.save("target/FillFormField.pdf");
    pdfDocument.close();

## Apache FOP

[Apache FOP (Formatting Objects Processor)](https://xmlgraphics.apache.org/fop/) is a print formatter and PDF generator
driven by XSL formatting objects (XSL-FO) and an output independent formatter.

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-fop</artifactId>
    </dependency>

## Exposed Spring Beans

The following Spring beans are exposed by the Girders mail module:

| Bean | Description |
|:--------|:------------|
| fopTemplate | An instance of [FopTemplate](../apidocs/com/netcetera/girders/fop/FopTemplate.html). This template
contains methods that you can use to transform XSL:FO and XML documents into PDFs. |

## Configuration Properties

The following properties are relevant for the configuration of the Girders mail module:

| Property | Default | Description |
|:---------|:--------|:------------|
| girders.fop.base | n/a | Default base URI that FOP uses for resolving URIs against (mandatory). |
| girders.fop.config | n/a | Resource that contains the configuration for FOP (mandatory). |
