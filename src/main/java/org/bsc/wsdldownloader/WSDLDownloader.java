package org.bsc.wsdldownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author softphone
 */
public class WSDLDownloader
{

    static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    private static Document parse( URL url ) throws ParserConfigurationException, IOException, SAXException {

        if( url==null ) throw new IllegalArgumentException( "url is null!");

        DocumentBuilder db = dbf.newDocumentBuilder();

        System.out.printf("download url [%s]\n", url );

        InputStream is = url.openStream();

        String file = url.getFile();

        int index = file.lastIndexOf('/');
        if( index!=-1 ) {
            file = file.substring(++index);
        }

        file = file.replace('?', '.').replace('=', '.');

        Document doc = db.parse( is );

        doc.getDocumentElement().normalize();

        is.close();

        return doc;
    }

    private static String parseLocationURL( URL location ) {
        if( location==null ) throw new IllegalArgumentException( "location is null!");

        String file = location.getFile();

        int index = file.lastIndexOf('/');
        if( index!=-1 ) {
            file = file.substring(++index);
        }

        file = file.replace('?', '.').replace('=', '.');

        return file;
    }
    
    private static File saveUrl( URL url ) throws IOException {

        InputStream is = url.openStream();

        String file = url.getFile();

        int index = file.lastIndexOf('/');
        if( index!=-1 ) {
            file = file.substring(++index);
        }

        file = file.replace('?', '.').replace('=', '.');

        File target = new File("target");
        File dump = new File( target, file);

        FileOutputStream pos = new FileOutputStream( dump );
        int c;
        while( (c = is.read())!=-1 ) {
            pos.write(c);
        }

        pos.close();
        is.close();

        return dump;

    }

    public static void writeXmlFile(Document doc, String filename) throws TransformerConfigurationException, TransformerException {
            File file = new File(filename);

            if( file.exists() ) {
                System.out.printf( "file [%s] already exists. Skipped!\n", file );
                return;
            }
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            Result result = new StreamResult(file);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();

            System.out.printf("writing file [%s]\n", file );

            
            xformer.transform(source, result);

    }

    private static java.net.URI resolveURI( java.net.URL from, String to )  {

        try { 
            java.net.URI locationURI = new java.net.URI( to );

            if( locationURI.isAbsolute() ) {
                return locationURI;
            }

            return from.toURI().resolve(locationURI);
        
        } catch( URISyntaxException e ) {

            System.out.printf("error parsing URI [%s]. It will be ignored! \n", to );
            e.printStackTrace( System.err );
            
        }
        return null;
    }

    private static void downloadAndParse( URL url  ) throws ParserConfigurationException, java.io.IOException, SAXException, TransformerConfigurationException, TransformerException {

        if( url==null ) throw new IllegalArgumentException( "url is null!");

        System.out.printf("download url [%s]\n", url );

        //File f = saveUrl( url);

        //System.out.printf("parse file [%s]\n", f );

        //Document doc = db.parse( f );

        Document doc = parse( url );

        final NodeList wsdlImport = doc.getElementsByTagName("wsdl:import");

        for( int i=0; i < wsdlImport.getLength() ; ++i ) {

          Element e = (Element) wsdlImport.item(i);

          String location = e.getAttribute("location");

          System.out.printf("wsdl:import location=[%s]\n", location );

          java.net.URI uri = resolveURI( url, location );    
          if( uri != null ) {  
              
              final URL locationURL = uri.toURL();

              String newLocation = parseLocationURL( locationURL );

              e.setAttribute("location", newLocation);

              downloadAndParse( locationURL );
          }
        }
        
        final NodeList xsdImport = doc.getElementsByTagName("xsd:import");

        for( int i=0; i < xsdImport.getLength() ; ++i ) {

          Element e = (Element) xsdImport.item(i);

          String location = e.getAttribute("schemaLocation");

          System.out.printf("xsd:import location=[%s]\n", location );

          java.net.URI uri = resolveURI( url, location );    
          if( uri != null ) {  

              final URL locationURL = uri.toURL();

              String newLocation = parseLocationURL( locationURL );

              e.setAttribute("schemaLocation", newLocation);

              downloadAndParse( locationURL );
          }
        }

        final NodeList xsInclude = doc.getElementsByTagName("xs:include");

        for( int i=0; i < xsInclude.getLength() ; ++i ) {

          Element e = (Element) xsInclude.item(i);

          String location = e.getAttribute("schemaLocation");

          System.out.printf("xsd:include location=[%s]\n", location );

          java.net.URI uri = resolveURI( url, location );    
          if( uri != null ) {  

              final URL locationURL = uri.toURL();

              String newLocation = parseLocationURL( locationURL );

              e.setAttribute("schemaLocation", newLocation);

              downloadAndParse( locationURL );
          }
        }

        final NodeList xsImport = doc.getElementsByTagName("xs:import");

        for( int i=0; i < xsImport.getLength() ; ++i ) {

          Element e = (Element) xsImport.item(i);

          String location = e.getAttribute("schemaLocation");

          System.out.printf("xs:import location=[%s]\n", location );

          java.net.URI uri = resolveURI( url, location );    
          if( uri != null ) {  

              final URL locationURL = uri.toURL();

              String newLocation = parseLocationURL( locationURL );

              e.setAttribute("schemaLocation", newLocation);

              downloadAndParse( locationURL );
          }
        }
        
        final NodeList xsRedefine = doc.getElementsByTagName("xs:redefine");

        for( int i=0; i < xsRedefine.getLength() ; ++i ) {

          Element e = (Element) xsRedefine.item(i);

          String location = e.getAttribute("schemaLocation");

          System.out.printf("xs:redefine location=[%s]\n", location );

          java.net.URI uri = resolveURI( url, location );    
          if( uri != null ) {  

              final URL locationURL = uri.toURL();

              String newLocation = parseLocationURL( locationURL );

              e.setAttribute("schemaLocation", newLocation);

              downloadAndParse( locationURL );
          }
        }

        String file = parseLocationURL( url );

        writeXmlFile( doc, file );
    }

    public static void main( String[] args ) 
    {
      if( args.length < 1  ) {
          
          System.out.println( "usage:  java -jar <WSDLDownloader archive>.jar <WSDL URL>");
          System.exit(-1);
      }
        try {
            downloadAndParse(new URL(args[0]));
        } catch (Exception ex) {
            Logger.getLogger(WSDLDownloader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

      System.exit(0);
      
      
      //downloadAndParse(new URL(" http://172.16.3.39/IVRServices/PosteItaliane.ContactCenter.Services.IVR.Accettazione.svc?WSDL"));
      //downloadAndParse(new URL("http://172.16.3.39/IVRServices/PosteItaliane.ContactCenter.Services.IVR.ServiziAggiuntivi.svc?WSDL"));

    }
}

