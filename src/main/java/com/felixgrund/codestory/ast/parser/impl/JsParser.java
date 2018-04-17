package com.felixgrund.codestory.ast.parser.impl;import com.felixgrund.codestory.ast.entities.Yfunction;import com.felixgrund.codestory.ast.entities.Yparameter;import com.felixgrund.codestory.ast.entities.Yreturn;import com.felixgrund.codestory.ast.exceptions.ParseException;import com.felixgrund.codestory.ast.parser.AbstractParser;import com.felixgrund.codestory.ast.parser.Yparser;import jdk.nashorn.internal.ir.FunctionNode;import jdk.nashorn.internal.ir.IdentNode;import jdk.nashorn.internal.ir.visitor.SimpleNodeVisitor;import jdk.nashorn.internal.parser.Parser;import jdk.nashorn.internal.runtime.Context;import jdk.nashorn.internal.runtime.ErrorManager;import jdk.nashorn.internal.runtime.Source;import jdk.nashorn.internal.runtime.options.Options;import java.util.ArrayList;import java.util.List;public class JsParser extends AbstractParser implements Yparser {	private Options parserOptions;	private FunctionNode rootFunctionNode;	public JsParser(String fileName, String fileContent) {		super(fileName, fileContent);		this.parserOptions = new Options("nashorn");		this.parserOptions.set("anon.functions", true);		this.parserOptions.set("parse.only", true);		this.parserOptions.set("scripting", true);		this.parserOptions.set("language", "es6");	}	@Override	public void parse() throws ParseException {		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();		ErrorManager errorManager = new ErrorManager();		Context context = new Context(this.parserOptions, errorManager, classLoader);		Source source = Source.sourceFor(this.fileName, this.fileContent);		Parser originalParser = new Parser(context.getEnv(), source, errorManager);		this.rootFunctionNode = originalParser.parse();		if (this.rootFunctionNode == null) {			throw new ParseException("Could not parse root function node", this.fileName, this.fileContent);		}	}	@Override	public Yfunction findFunctionByNameAndLine(String name, int line) {		Yfunction ret = null;		FunctionNode node = findFunction(new FunctionNodeVisitor() {			@Override			public boolean nodeMatches(FunctionNode functionNode) {				String functionIdent = functionNode.getIdent().getName();				return functionNode.getLineNumber() == line && functionNode.getIdent().getName().equals(name);			}		});		if (node != null) {			ret = new Yfunction(name, getFunctionBody(node), getFunctionParameters(node), new Yreturn(Yreturn.TYPE_NONE));		}		return ret;	}	@Override	public List<Yfunction> findFunctionsByOtherFunction(Yfunction otherFunction) {		List<Yfunction> functions = new ArrayList<>();		String functionNameOther = otherFunction.getName();		List<Yparameter> parametersOther = otherFunction.getParameters();		List<FunctionNode> matchedFunctions = findAllFunctions(new FunctionNodeVisitor() {			@Override			public boolean nodeMatches(FunctionNode functionNode) {				List<Yparameter> parametersThis = getFunctionParameters(functionNode);				String functionNameThis = functionNode.getIdent().getName();				boolean nameMatches = functionNameThis.equals(functionNameOther);				boolean paramsMatch = parametersThis.equals(parametersOther);				return nameMatches && paramsMatch;			}		});		for (FunctionNode node : matchedFunctions) {			String name = node.getIdent().getName();			String body = getFunctionBody(node);			List<Yparameter> parameters = getFunctionParameters(node);			functions.add(new Yfunction(name, body, parameters, new Yreturn(null)));		}		return functions;	}	private FunctionNode findFunction(FunctionNodeVisitor visitor) {		FunctionNode ret = null;		this.rootFunctionNode.accept(visitor);		List<FunctionNode> matchedNodes = visitor.getMatchedNodes();		if (matchedNodes.size() > 0) {			ret = matchedNodes.get(0);		}		return ret;	}	private List<FunctionNode> findAllFunctions(FunctionNodeVisitor visitor) {		this.rootFunctionNode.accept(visitor);		return visitor.getMatchedNodes();	}	private String getFunctionBody(FunctionNode functionNode) {		String fileSource = functionNode.getSource().getString();		return fileSource.substring(functionNode.getStart(), functionNode.getFinish());	}	private List<Yparameter> getFunctionParameters(FunctionNode functionNode) {		List<Yparameter> parameters = new ArrayList<>();		List<IdentNode> parameterNodes = functionNode.getParameters();		for (IdentNode node : parameterNodes) {			parameters.add(new Yparameter(node.getName(), Yparameter.TYPE_NONE));		}		return parameters;	}	private abstract class FunctionNodeVisitor extends SimpleNodeVisitor {		private List<FunctionNode> matchedNodes = new ArrayList<>();		public abstract boolean nodeMatches(FunctionNode functionNode);		@Override		public boolean enterFunctionNode(FunctionNode functionNode) {			if (nodeMatches(functionNode)) {				matchedNodes.add(functionNode);			}			return true;		}		public List<FunctionNode> getMatchedNodes() {			return matchedNodes;		}	}}