<?xml version="1.0" encoding="utf-8"?>

<!-- Simple Stylesheet for converting  XHTML (basically just h1 and p) to XSL-FO. -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0"
                xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd">

  <xsl:template match="html:html">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="a4-portrait" page-height="297mm" page-width="210mm">
          <fo:region-body region-name="body" margin="10mm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <xsl:apply-templates select="html:body"/>
    </fo:root>
  </xsl:template>

  <xsl:template match="html:body">
    <fo:page-sequence master-reference="a4-portrait">
      <fo:flow flow-name="body" font-size="14pt" line-height="18pt">
        <fo:block>
          <fo:external-graphic src="url('logo.jpg')"/>
        </fo:block>
        <xsl:apply-templates/>
      </fo:flow>
    </fo:page-sequence>
  </xsl:template>

  <xsl:template match="html:h1">
    <fo:block text-align="center" font-size="24pt" line-height="28pt" font-weight="bold">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="html:p">
    <fo:block text-align="justify">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

</xsl:stylesheet>
