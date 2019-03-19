package management;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XMLLoader{
	private Document document;
	private String filename;
	
	public void updateXML()
	{
		XMLOutputter outputFile = new XMLOutputter();
		outputFile.setFormat(Format.getPrettyFormat());
		try {
			outputFile.output(document, new FileWriter(PathFinder.getProjectPath() + "settings.xml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isStringChild(Element child, String childName)
	{
		if(child.getName().equals(childName))
			return true;
		
		return false;
	}

	public void FXMLStructure()
	{
		
	}
	
	public void createXML(XMLLoadable object)
	{
		if(new File(PathFinder.getProjectPath() + filename).exists())
			return;
		object.XMLStructure();
		
		this.updateXML();
	}
	
	public void setLayout()
	{
		FXMLStructure();
	}
	
	public Document getDocument()
	{
		return this.document;
	}
	
	public void addNode(String[] parentNodes, String[] childNodes, String content[])
	{
		Element[] contents = new Element[content.length];
		int i;
		for(i = 0; i < contents.length; i++)
		{
			contents[i] = new Element(childNodes[i]);
			contents[i].setText(content[i]);
		}
		
		Element parent = document.getRootElement();
		
		i = 0;
		if(parentNodes[0].equals(this.document.getRootElement().getName()))
			i++;
		for( ; i < parentNodes.length; i++)
			parent = parent.getChild(parentNodes[i]);
		
		for(i = 0; i < contents.length; i++)
			parent.addContent(contents[i]);
			
		this.updateXML();
	}
	
	public void addNode(String[] parentNodes, Element childNode)
	{
		Element parent = document.getRootElement();
		int i = 0;
		if(parentNodes[0].equals(this.document.getRootElement().getName()))
			i++;
		for( ; i < parentNodes.length; i++)
			parent = parent.getChild(parentNodes[i]);
		
		parent.addContent(childNode);
			
		this.updateXML();
	}
	
	public int getNodesNumber(String[] parentNodes)
	{
		Element parent = document.getRootElement();
		int i = 0;
		if(parentNodes[0].equals(this.document.getRootElement().getName()))
			i++;
		for( ; i < parentNodes.length; i++)
			parent = parent.getChild(parentNodes[i]);
		
		return parent.getChildren().size();
	}
	
	public List<Element> getNodesList(String[] parentNodes)
	{
		Element parent = document.getRootElement();
		int i = 0;
		if(parentNodes[0].equals(this.document.getRootElement().getName()))
			i++;
		for( ; i < parentNodes.length; i++)
			parent = parent.getChild(parentNodes[i]);
		
		return parent.getChildren();
	}
	
	public Element getNodePointer(String[] parentNodes)
	{
		Element parent = document.getRootElement();
		int i = 0;
		if(parentNodes[0].equals(this.document.getRootElement().getName()))
			i++;
		for( ; i < parentNodes.length; i++)
			parent = parent.getChild(parentNodes[i]);
		
		return parent;
	}
	
	public void removeNode(String[] parentNodes, int nodeNumber)
	{
		
	}
	
	public XMLLoader(String filename)
	{
		SAXBuilder builder = new SAXBuilder();
		try {
			this.document = (Document) builder.build(new File(PathFinder.getProjectPath() + "settings.xml"));
		} catch (JDOMException | IOException e) {
			this.document = new Document();
		}
		this.filename = filename;
	}
}
