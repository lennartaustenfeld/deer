package org.aksw.deer.enrichments;

import com.github.therapi.runtimejavadoc.RetainJavadoc;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.aksw.deer.vocabulary.DEER;
import org.aksw.faraday_cage.engine.ValidatableParameterMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.jetbrains.annotations.NotNull;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * An EnrichmentOperator for dereferencing.
 * <p>
 * Dereferencing is a method of expanding knowledge of resources that belong to an
 * external knowledge base.
 * We define a dereferencing operation as the following sequence of operations:
 * <ol>
 *     <li>Query the local model for starting resources {@code ?x}
 *         that belong to an external knowledge base</li>
 *     <li>Query the external knowledge base for triples {@code (?x, d, ?y)},
 *         where d is a defined property path in the external knowledge base.</li>
 *     <li>Add all results to the local model in the form of {@code (?x, i, ?y)},
 *         where i is the property name under which they should be imported.</li>
 * </ol>
 * <h2>Configuration</h2>
 *
 * <h3>{@code :operations}</h3>
 *
 *
 *
 * Each operation corresponds to one dereferencing operation, allowing multiple
 * dereferencing operations being carried out by a single {@code DereferencingEnrichmentOperator}.
 * Each entry may contain the following properties:
 *
 * <blockquote>
 *     <b>{@code :lookUpProperty}</b>
 *     <i>range: resource</i>
 *     <br>
 *     Determines the starting resources {@code ?x} as all objects of triples having
 *     the value of {@code :lookUpProperty} as predicate.
 * </blockquote>
 *
 * <blockquote>
 *     <b>{@code :lookUpPrefix} [required]</b>
 *     <i>range: string</i>
 *     <br>
 *     Determines the starting resources {@code ?x} as all objects of triples having
 *     the value of {@code :lookUpProperty} as predicate.
 * </blockquote>
 *
 * <blockquote>
 *     <b>{@code :dereferencingProperty} [required]</b>
 *     <i>range: resource</i>
 *     <br>
 *     Look up the values to be imported to the local model in the external knowledge base
 *     using the property defined by the value of {@code :dereferencingProperty}.
 * </blockquote>
 *
 * <blockquote>
 *     <b>{@code :importProperty} [required]</b>
 *     <i>range: resource</i>
 *     <br>
 *     Add looked up values to starting resources using the value of :importProperty.
 * </blockquote>
 *
 *
 * <h2>Example</h2>
 *
 */
@Extension @RetainJavadoc
public class DereferencingEnrichmentOperator extends AbstractParameterizedEnrichmentOperator {

//   * <blockquote>
//   *     <b>{@code :endpoint} </b>
//   *     <i>range: string</i>
//   *     <br>
//   *     URL of the SPARQL endpoint to use for this dereferencing operation.
//   *     If not given, this operator tries to infer the endpoint from the starting resources URIs.
//   * </blockquote>



  private static final Logger logger = LoggerFactory.getLogger(DereferencingEnrichmentOperator.class);

  public static final Property LOOKUP_PROPERTY = DEER.property("lookUpProperty");

  public static final Property LOOKUP_PREFIX = DEER.property("lookUpPrefix");

  public static final Property DEREFERENCING_PROPERTY = DEER.property("dereferencingProperty");

  public static final Property IMPORT_PROPERTY = DEER.property("importProperty");

  public static final Property OPERATION = DEER.property("operation");

//  public static final Parameter OPERATIONS = new DeerParameter("operations",
//    new DictListParameterConversion(LOOKUP_PREFIX, LOOKUP_PROPERTY, DEREFERENCING_PROPERTY,
//      IMPORT_PROPERTY), true);

  private static final String DEFAULT_LOOKUP_PREFIX = "http://dbpedia.org/resource";

  private static final Set<Property> ignoredProperties = new HashSet<>(Arrays.asList(OWL.sameAs));

  private HashMap<OperationGroup, Set<Property[]>> operations;

  private Model model;

  @NotNull
  @Override
  public ValidatableParameterMap createParameterMap() {
    return ValidatableParameterMap.builder()
      .declareProperty(OPERATION)
      .declareValidationShape(getValidationModelFor(DereferencingEnrichmentOperator.class))
      .build();
  }

//  /**
//   * Self configuration
//   * Find source/target URI as the most redundant URIs
//   *
//   * @param source source
//   * @param target target
//   *
//   * @return Map of (key, value) pairs of self configured parameters
//   */
//  @NotNull
//  @Override
//  public ParameterMap selfConfig(Model source, Model target) {
//    ParameterMap parameters = createParameterMap();
//    Set<Property> propertyDifference = getPropertyDifference(source, target);
//    List<Map<Property, RDFNode>> autoOperations = new ArrayList<>();
//    for (Property property : propertyDifference) {
//      Map<Property, RDFNode> autoOperation = new HashMap<>();
//      autoOperation.put(DEREFERENCING_PROPERTY, property);
//      autoOperation.put(IMPORT_PROPERTY, property);
//      autoOperations.add(autoOperation);
//    }
//    parameters.setValue(OPERATIONS, autoOperations);
//    return parameters;
//  }

  public void initializeOperations() {
    ValidatableParameterMap parameters = getParameterMap();
    this.operations = new HashMap<>();
    parameters.listPropertyObjects(OPERATION)
      .map(RDFNode::asResource)
      .forEach(op -> {
      String lookUpPrefix = !op.hasProperty(LOOKUP_PREFIX)
        ? DEFAULT_LOOKUP_PREFIX : op.getProperty(LOOKUP_PREFIX).getLiteral().getString();
      Property lookUpProperty = !op.hasProperty(LOOKUP_PROPERTY)
        ? null : op.getPropertyResourceValue(LOOKUP_PROPERTY).as(Property.class);
      Property dereferencingProperty = !op.hasProperty(DEREFERENCING_PROPERTY)
        ? null : op.getPropertyResourceValue(DEREFERENCING_PROPERTY).as(Property.class);
      Property importProperty = !op.hasProperty(IMPORT_PROPERTY)
        ? dereferencingProperty : op.getPropertyResourceValue(IMPORT_PROPERTY).as(Property.class);
      OperationGroup opGroup = new OperationGroup(lookUpProperty, lookUpPrefix);
      if (!operations.containsKey(opGroup)) {
        operations.put(opGroup, new HashSet<>());
      }
      operations.get(opGroup).add(new Property[]{dereferencingProperty, importProperty});
    });
  }

  @Override
  protected List<Model> safeApply(List<Model> models) {
    initializeOperations();
    model = ModelFactory.createDefaultModel().add(models.get(0));
    operations.forEach(this::runOperation);
    return Lists.newArrayList(model);
  }

  private void runOperation(OperationGroup opGroup, Set<Property[]> ops) {
    Map<Resource, List<Pair<Property, RDFNode>>> dereffedPerResource = new HashMap<>();
    List<Statement> candidateNodes = getCandidateNodes(opGroup.lookupPrefix, opGroup.lookupProperty);
    candidateNodes.stream()
      .map(Statement::getResource)
      .distinct()
      .forEach(o -> dereffedPerResource.put(o, getEnrichmentPairsFor(o, ops)));
    for (Statement stmt : candidateNodes) {
      for (Pair<Property, RDFNode> pair : dereffedPerResource.get(stmt.getResource())) {
        stmt.getSubject().addProperty(pair.getLeft(), pair.getRight());
      }
    }
  }

  private List<Pair<Property, RDFNode>> getEnrichmentPairsFor(Resource o, Set<Property[]> ops) {
    List<Pair<Property, RDFNode>> result = new ArrayList<>();
    Model resourceModel = queryResourceModel(o);
    for (Property[] op : ops) {
      resourceModel.listStatements(o, op[0], (RDFNode)null)
        .mapWith(Statement::getObject)
        .forEachRemaining(x -> result.add(new ImmutablePair<>(op[1], x)));
    }
    return result;
  }

  private Model queryResourceModel(Resource o) {
    Model result = ModelFactory.createDefaultModel();
    URL url;
    URLConnection conn;

//    try (RDFConnection conn = RDFConnectionRemote
//      .create()
//      .destination(o.getURI())
//      .acceptHeaderGraph("application/rdf+xml")
//      .build()) {
//      return conn.fetch();
//    }

    try {
      url = new URL(o.getURI());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return result;
    }
    try {
      conn = url.openConnection();
      conn.setRequestProperty("Accept", "application/rdf+xml");
      conn.setRequestProperty("Accept-Language", "en");
      return ModelFactory.createDefaultModel()
        .read(conn.getInputStream(), null);
    } catch (ConnectException e) {
      if (e.getMessage().contains("refused")) {
        throw new RuntimeException(e);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  private List<Statement> getCandidateNodes(String lookupPrefix, Property lookUpProperty) {
    return model.listStatements()
      .filterKeep(statement -> statement.getObject().isURIResource() &&
        statement.getObject().asResource().getURI().startsWith(lookupPrefix) &&
        (lookUpProperty == null || statement.getPredicate().equals(lookUpProperty)))
      .toList();
  }

  private Set<Property> getPropertyDifference(Model source, Model target) {
    Function<Model, Set<Property>> getProperties =
      (m) -> m.listStatements().mapWith(Statement::getPredicate).toSet();
    return Sets.difference(getProperties.apply(target), getProperties.apply(source))
      .stream().filter(p -> !ignoredProperties.contains(p)).collect(Collectors.toSet());
  }

  private static class OperationGroup {

    private final Property lookupProperty;
    private final String lookupPrefix;

    private OperationGroup(Property lookupProperty, String lookupPrefix) {
      this.lookupProperty = lookupProperty;
      this.lookupPrefix = lookupPrefix;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof  OperationGroup)) {
        return false;
      }
      OperationGroup other = (OperationGroup) obj;
      return (Objects.equals(lookupPrefix, other.lookupPrefix))
        && (Objects.equals(lookupProperty, other.lookupProperty));
    }

    @Override
    public int hashCode() {
      return (lookupPrefix == null ? -1 : lookupPrefix.hashCode()) + 13 * (lookupProperty == null ? -1 : lookupProperty.hashCode());
    }
  }

}
