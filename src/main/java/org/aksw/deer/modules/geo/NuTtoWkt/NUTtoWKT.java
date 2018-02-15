//package org.aksw.deer.modules.geo.NuTtoWkt;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import org.aksw.deer.io.Reader;
//import org.apache.jena.query.QueryExecution;
//import org.apache.jena.query.QueryExecutionFactory;
//import org.apache.jena.query.QueryFactory;
//import org.apache.jena.query.QuerySolution;
//import org.apache.jena.query.ResultSet;
//import org.apache.jena.rdf.model.AnonId;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.ModelFactory;
//import org.apache.jena.rdf.model.RDFNode;
//import org.apache.jena.rdf.model.Resource;
//import org.apache.jena.rdf.model.ResourceFactory;
//import org.apache.jena.vocabulary.RDF;
//
//import com.vividsolutions.jts.io.ParseException;
//
//
//
//public class NUTtoWKT {
//
//	public static void main(String[] args) throws IOException, ParseException {
//
//		String ngeo = "http://geovocab.org/geometry#";
//		String dc   = "http://purl.org/dc/elements/1.1/" ;
//		double d1=0.0447 ;
//		double d2=1.9447;
//
//		String fileName = "/home/abddatascienceadmin/deer/NUT_DATA/DE_geometry_out3.ttl";
//		Model model= Reader.readModel(fileName);
//
//
//		ArrayList<String> allSubjects= new ArrayList<String>();
//		ArrayList<RDFNode> allPM= new ArrayList<RDFNode>();
//		ArrayList<RDFNode> allEX= new ArrayList<RDFNode>();
//		ArrayList<String> allPL= new ArrayList<String>();
//
//		String sparqlQueryString =
//
//				"SELECT ?s ?pm ?ex ?pl"+" "
//						+ "WHERE{"
//						+ "?s <" + ngeo+"polygonMember> ?pm . "
//						+ "?pm <" + ngeo+"exterior> ?ex . "
//						+ "?ex <" + ngeo+"toWKT> ?pl . "
//
//						+ "}" ;
//
//		QueryFactory.create(sparqlQueryString);
//		QueryExecution qexec = QueryExecutionFactory.create(sparqlQueryString, model);
//		ResultSet queryResults = qexec.execSelect();
//
//		while(queryResults.hasNext()){
//
//			QuerySolution qs=queryResults.nextSolution();
//
//			allSubjects.add(qs.get("s").toString());
//			allPM.add(qs.get("pm"));
//			allEX.add(qs.get("ex"));
//			allPL.add(qs.get("pl").toString());
//
//		}
//		ArrayList<String>firstGeomsG = GeomSimplification.geomSimpilfication(allPL,d1);
//		//ArrayList<String>secondGeomsG = GeomSimplification.geomSimpilfication(allPL,1.9447);
//		ArrayList<String>allPl = GeomSimplification.geomMean(allPL);
//		System.out.println("secondGeomsG: "+ allPl.get(0));
//
//		System.out.println("secondGeomsG1: "+ allPl.get(1));
//
//		System.out.println("secondGeomsG2: "+ allPl.get(2));
//
//		System.out.println("secondGeomsG3: "+ allPl.get(3));
//
//		Model model2 = ModelFactory.createDefaultModel();
//
//		ArrayList<Resource> rootResources=new ArrayList<Resource>();
//		rootResources.add(ResourceFactory.createResource(allSubjects.get(0)));
//		rootResources.add(ResourceFactory.createResource(allSubjects.get(1)));
//		rootResources.add(ResourceFactory.createResource(allSubjects.get(2)));
//		rootResources.add(ResourceFactory.createResource(allSubjects.get(3)));
//
//
//		//if(n_2=true) {
//		model2.add(rootResources.get(0),ResourceFactory.createProperty(dc, "right"),
//				ResourceFactory.createStringLiteral("© EuroGeographics for the administrative boundaries." ));
//
//		model2.add(rootResources.get(0),RDF.type,ResourceFactory.createResource(ngeo+"MultiPolygon"));
//
//		AnonId n1 = AnonId.create("_:123");
//		Resource r1 = model2.createResource(n1);
//		model2.add(rootResources.get(0),ResourceFactory.createProperty(ngeo, "polygonMember"),r1);
//		model2.add(r1,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//
//		AnonId n2 = AnonId.create("_:124");
//		Resource r2 = model2.createResource(n2);
//		model2.add(r1,ResourceFactory.createProperty(ngeo, "exterior"),r2);
//		model2.add(r2,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		model2.add(r2,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPl.get(0)));
//
//
//		AnonId n3 = AnonId.create("_:125");
//		Resource r3 = model2.createResource(n3);
//		model2.add(rootResources.get(1),ResourceFactory.createProperty(ngeo, "polygonMember"),r3);
//		model2.add(r3,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//
//		AnonId n4 = AnonId.create("_:126");
//		Resource r4 = model2.createResource(n4);
//		model2.add(r3,ResourceFactory.createProperty(ngeo, "exterior"),r4);
//		model2.add(r4,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		model2.add(r4,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPl.get(1)));
//
//
//		AnonId n5 = AnonId.create("_:127");
//		Resource r5 = model2.createResource(n5);
//		model2.add(rootResources.get(2),ResourceFactory.createProperty(ngeo, "polygonMember"),r5);
//		model2.add(r5,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//
//		AnonId n6 = AnonId.create("_:128");
//		Resource r6 = model2.createResource(n6);
//		model2.add(r5,ResourceFactory.createProperty(ngeo, "exterior"),r6);
//		model2.add(r6,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		model2.add(r6,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPl.get(2)));
//
//
//
//		AnonId n7 = AnonId.create("_:129");
//		Resource r7 = model2.createResource(n7);
//		model2.add(rootResources.get(3),ResourceFactory.createProperty(ngeo, "polygonMember"),r7);
//		model2.add(r7,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//
//		AnonId n8 = AnonId.create("_:130");
//		Resource r8 = model2.createResource(n8);
//		model2.add(r7,ResourceFactory.createProperty(ngeo, "exterior"),r8);
//		model2.add(r8,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		model2.add(r8,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPl.get(3)));
//
//		String outputFile12= "/home/abddatascienceadmin/deer/NUT_DATA/DE_geometry_out_mean.nt";
//		model2.write(new FileOutputStream(outputFile12), "NT", null);
//		//}
//
//		//		if(n_4=true) {
//		//			model2.add(rootResources.get(0),ResourceFactory.createProperty(dc, "right"),
//		//					ResourceFactory.createStringLiteral("© EuroGeographics for the administrative boundaries." ));
//		//
//		//			model2.add(rootResources.get(0),RDF.type,ResourceFactory.createResource(ngeo+"MultiPolygon"));
//		//
//		//			AnonId n1 = AnonId.create("_:123");
//		//			Resource r1 = model2.createResource(n1);
//		//			model2.add(rootResources.get(0),ResourceFactory.createProperty(ngeo, "polygonMember"),r1);
//		//			model2.add(r1,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//		//
//		//			AnonId n2 = AnonId.create("_:124");
//		//			Resource r2 = model2.createResource(n2);
//		//			model2.add(r1,ResourceFactory.createProperty(ngeo, "exterior"),r2);
//		//			model2.add(r2,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		//			model2.add(r2,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(secondGeomsG.get(0)));
//		//
//		//
//		//			AnonId n3 = AnonId.create("_:125");
//		//			Resource r3 = model2.createResource(n3);
//		//			model2.add(rootResources.get(1),ResourceFactory.createProperty(ngeo, "polygonMember"),r3);
//		//			model2.add(r3,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//		//
//		//			AnonId n4 = AnonId.create("_:126");
//		//			Resource r4 = model2.createResource(n4);
//		//			model2.add(r3,ResourceFactory.createProperty(ngeo, "exterior"),r4);
//		//			model2.add(r4,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		//			model2.add(r4,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(secondGeomsG.get(1)));
//		//
//		//
//		//			AnonId n5 = AnonId.create("_:127");
//		//			Resource r5 = model2.createResource(n5);
//		//			model2.add(rootResources.get(2),ResourceFactory.createProperty(ngeo, "polygonMember"),r5);
//		//			model2.add(r5,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//		//
//		//			AnonId n6 = AnonId.create("_:128");
//		//			Resource r6 = model2.createResource(n6);
//		//			model2.add(r5,ResourceFactory.createProperty(ngeo, "exterior"),r6);
//		//			model2.add(r6,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		//			model2.add(r6,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(secondGeomsG.get(2)));
//		//
//		//
//		//
//		//			AnonId n7 = AnonId.create("_:129");
//		//			Resource r7 = model2.createResource(n7);
//		//			model2.add(rootResources.get(3),ResourceFactory.createProperty(ngeo, "polygonMember"),r7);
//		//			model2.add(r7,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//		//
//		//			AnonId n8 = AnonId.create("_:130");
//		//			Resource r8 = model2.createResource(n8);
//		//			model2.add(r7,ResourceFactory.createProperty(ngeo, "exterior"),r8);
//		//			model2.add(r8,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		//			model2.add(r8,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(secondGeomsG.get(3)));
//		//
//		//			String outputFile12= "/home/abddatascienceadmin/deer/NUT_DATA/DE_geometry_out_N_4.nt";
//		//			model2.write(new FileOutputStream(outputFile12), "NT", null);
//		//
//		//		}
//		//		if(mean=true) {
//		//			model2.add(rootResources.get(0),ResourceFactory.createProperty(dc, "right"),
//		//					ResourceFactory.createStringLiteral("© EuroGeographics for the administrative boundaries." ));
//		//
//		//			model2.add(rootResources.get(0),RDF.type,ResourceFactory.createResource(ngeo+"MultiPolygon"));
//		//
//		//			AnonId n1 = AnonId.create("_:123");
//		//			Resource r1 = model2.createResource(n1);
//		//			model2.add(rootResources.get(0),ResourceFactory.createProperty(ngeo, "polygonMember"),r1);
//		//			model2.add(r1,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//		//
//		//			AnonId n2 = AnonId.create("_:124");
//		//			Resource r2 = model2.createResource(n2);
//		//			model2.add(r1,ResourceFactory.createProperty(ngeo, "exterior"),r2);
//		//			model2.add(r2,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		//			model2.add(r2,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPl.get(0)));
//		//
//		//
//		//			AnonId n3 = AnonId.create("_:125");
//		//			Resource r3 = model2.createResource(n3);
//		//			model2.add(rootResources.get(1),ResourceFactory.createProperty(ngeo, "polygonMember"),r3);
//		//			model2.add(r3,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//		//
//		//			AnonId n4 = AnonId.create("_:126");
//		//			Resource r4 = model2.createResource(n4);
//		//			model2.add(r3,ResourceFactory.createProperty(ngeo, "exterior"),r4);
//		//			model2.add(r4,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		//			model2.add(r4,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPl.get(1)));
//		//
//		//
//		//			AnonId n5 = AnonId.create("_:127");
//		//			Resource r5 = model2.createResource(n5);
//		//			model2.add(rootResources.get(2),ResourceFactory.createProperty(ngeo, "polygonMember"),r5);
//		//			model2.add(r5,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//		//
//		//			AnonId n6 = AnonId.create("_:128");
//		//			Resource r6 = model2.createResource(n6);
//		//			model2.add(r5,ResourceFactory.createProperty(ngeo, "exterior"),r6);
//		//			model2.add(r6,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		//			model2.add(r6,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPl.get(2)));
//		//
//		//
//		//
//		//			AnonId n7 = AnonId.create("_:129");
//		//			Resource r7 = model2.createResource(n7);
//		//			model2.add(rootResources.get(3),ResourceFactory.createProperty(ngeo, "polygonMember"),r7);
//		//			model2.add(r7,RDF.type,ResourceFactory.createResource(ngeo+"polygon"));
//		//
//		//			AnonId n8 = AnonId.create("_:130");
//		//			Resource r8 = model2.createResource(n8);
//		//			model2.add(r7,ResourceFactory.createProperty(ngeo, "exterior"),r8);
//		//			model2.add(r8,RDF.type,ResourceFactory.createResource(ngeo+"LinearRing"));
//		//			model2.add(r8,ResourceFactory.createProperty(ngeo, "toWKT"),ResourceFactory.createStringLiteral(allPl.get(3)));
//		//
//		//			String outputFile12= "/home/abddatascienceadmin/deer/NUT_DATA/DE_geometry_out_mean.nt";
//		//			model2.write(new FileOutputStream(outputFile12), "NT", null);
//		//		}
//	}
//
//
//
//
//}