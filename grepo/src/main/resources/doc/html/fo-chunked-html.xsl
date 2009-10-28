<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:xslthl="http://xslthl.sf.net"
    exclude-result-prefixes="xslthl"
    version="1.0">

    <xsl:import href="urn:docbkx:stylesheet" />

    <xsl:param name="html.stylesheet">resources/css/style.css</xsl:param>

    <!-- These extensions are required for table printing and other stuff -->
    <xsl:param name="use.extensions">1</xsl:param>
    <xsl:param name="tablecolumns.extension">0</xsl:param>
    <xsl:param name="callout.extensions">1</xsl:param>
    <xsl:param name="graphicsize.extension">0</xsl:param>

    <xsl:param name="generate.toc">book toc</xsl:param>
    <xsl:param name="toc.section.depth">4</xsl:param>

    <xsl:param name="chapter.autolabel">1</xsl:param>
    <xsl:param name="section.autolabel" select="1" />
    <xsl:param name="section.label.includes.component.label" select="1" />

    <xsl:param name="callout.graphics">0</xsl:param>
    <xsl:param name="callout.defaultcolumn">90</xsl:param>

    <xsl:param name="admon.graphics">0</xsl:param>

    <xsl:param name="formal.title.placement">
        figure after
        example before
        equation before
        table before
        procedure before
    </xsl:param>

    <!-- navigation icons -->
    <xsl:param name="navig.graphics">0</xsl:param>
    <xsl:param name="navig.graphics.path">resources/images/</xsl:param>
    <xsl:param name="navig.graphics.extension">.png</xsl:param>

    <!-- do not show titles
    <xsl:param name="navig.showtitles">1</xsl:param>
    -->

    <!-- place header logo  -->
    <xsl:template name="grepo.logo">
        <img src="resources/images/grepo-logo-big-200x40.png"/>
    </xsl:template>
    <xsl:template name="user.header.navigation">
        <xsl:call-template name="grepo.logo"/>
    </xsl:template>

    <!-- java source code -->
    <xsl:param name="highlight.source" select="1" />
    <xsl:template match='xslthl:string' mode="xslthl">
        <b class="hl-string"><xsl:apply-templates mode="xslthl"/></b>
    </xsl:template>
</xsl:stylesheet>