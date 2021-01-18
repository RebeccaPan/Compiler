package frontend;

import AST.*;
import Util.LocationType;
import Util.ScopeType;
import parser.MxBaseVisitor;
import parser.MxParser;

import java.util.ArrayList;

public class ASTBuilder extends MxBaseVisitor<ASTNode>{
	@Override public ASTNode visitLiteral(MxParser.LiteralContext ctx) {
		// return LiteralNode
		if (ctx.TRUE() != null)
			return new BoolLiteralNode(new ScopeType(ctx), new LocationType(ctx), true);
		if (ctx.FALSE()!= null)
			return new BoolLiteralNode(new ScopeType(ctx), new LocationType(ctx), false);
		if (ctx.NUM()  != null)
			return new IntLiteralNode (new ScopeType(ctx), new LocationType(ctx), Integer.parseInt(ctx.NUM().toString()));
		if (ctx.NULL() != null)
			return new NullLiteralNode(new ScopeType(ctx), new LocationType(ctx));
		if (ctx.STR()  != null)
			return new StrLiteralNode (new ScopeType(ctx), new LocationType(ctx), ctx.STR().toString());
		return null;
	}
    
	@Override public ASTNode visitProgram(MxParser.ProgramContext ctx) {
		// return ProgramNode
		ArrayList<DefNode> DefNodes = new ArrayList<>();
		for (var curDefNode : ctx.def()) {
			ASTNode cur = visit(curDefNode);
			DefNodes.add((DefNode) cur);
		}
        return new ProgramNode(new ScopeType(ctx), new LocationType(ctx), DefNodes);
    }
	
    @Override public ASTNode visitSuite(MxParser.SuiteContext ctx) {
        return visitChildren(ctx);
    }
    
	@Override public ASTNode visitBlockStmt(MxParser.BlockStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitVarDefStmt(MxParser.VarDefStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitIfStmt(MxParser.IfStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitForStmt(MxParser.ForStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitWhileStmt(MxParser.WhileStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitBreakStmt(MxParser.BreakStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitContinueStmt(MxParser.ContinueStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitReturnStmt(MxParser.ReturnStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitExprStmt(MxParser.ExprStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitEmptyStmt(MxParser.EmptyStmtContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitDef(MxParser.DefContext ctx) {
		// return ClassDefNode or FuncDefNode or VarDefNode
		if (ctx.classDef() != null) return visit(ctx.classDef());
		if (ctx.funcDef()  != null) return visit(ctx.funcDef());
		if (ctx.varDef()   != null) return visit(ctx.varDef());
		return null;
    }

	@Override public ASTNode visitClassDef(MxParser.ClassDefContext ctx) {
	    // return ClassDefNode
	    ClassDefNode ret = new ClassDefNode(new ScopeType(ctx), new LocationType(ctx), ctx.ID().getText());
	    for (var curVarDef : ctx.varDef()) {
	    	ASTNode cur = visit(curVarDef);
	    	ret.addVarDef((VarDefNode) cur);
	    }
		for (var curFuncDef : ctx.funcDef()) {
			ASTNode cur = visit(curFuncDef);
			ret.addFuncDef((FuncDefNode) cur);
		}
		for (var curConstructorDef : ctx.constructorDef()) {
			ASTNode cur = visit(curConstructorDef);
			ret.addConstructorDef((ConstructorDefNode) cur);
		}
    	return ret;
    }

	@Override public ASTNode visitFuncDef(MxParser.FuncDefContext ctx) {
		// return FuncDefNode
    	return new FuncDefNode(
    			new ScopeType(ctx),
				new LocationType(ctx),
				(TypeNode) visit(ctx.type()),
				ctx.ID().getText(),
				(ctx.paraList() != null) ? (ParaListNode) visit(ctx.paraList()) : null,
				(SuiteNode) visit(ctx.suite()) );
    }

	@Override public ASTNode visitVarDef(MxParser.VarDefContext ctx) {
		// return VarDefNode
		VarDefNode ret = new VarDefNode(new ScopeType(ctx), new LocationType(ctx), (TypeNode) visit(ctx.type()));
		for (var curSimpleVarDef : ctx.simpleVarDef()) {
			ASTNode cur = visit(curSimpleVarDef);
			ret.addSimpleVarDef((SimpleVarDefNode) cur);
		}
    	return ret;
    }

	@Override public ASTNode visitSimpleVarDef(MxParser.SimpleVarDefContext ctx) {
		// return SimpleVarDefNode
    	return new SimpleVarDefNode(
    			new ScopeType(ctx),
				new LocationType(ctx),
				ctx.ID().getText(),
				(ctx.expr() != null) ? (ExprNode) visit(ctx.expr()) : null );
    }

	@Override public ASTNode visitConstructorDef(MxParser.ConstructorDefContext ctx) {
		// return ConstructorDefNode
		return new ConstructorDefNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				ctx.ID().getText(),
				(ctx.paraList() != null) ? (ParaListNode) visit(ctx.paraList()) : null,
				(SuiteNode) visit(ctx.suite()) );
    }

	@Override public ASTNode visitNewExpr(MxParser.NewExprContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitPrefixExpr(MxParser.PrefixExprContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitClassMemberExpr(MxParser.ClassMemberExprContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitSubscriptExpr(MxParser.SubscriptExprContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitAtomExpr(MxParser.AtomExprContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitPostfixExpr(MxParser.PostfixExprContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitAssignExpr(MxParser.AssignExprContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitCallFuncExpr(MxParser.CallFuncExprContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitPrimary(MxParser.PrimaryContext ctx) {
		if (ctx.expr() != null)
			return visit(ctx.expr());
		if (ctx.THIS() != null)
			return new ThisNode(new ScopeType(ctx), new LocationType(ctx));
		if (ctx.ID() != null)
			return new IDNode(new ScopeType(ctx), new LocationType(ctx));
		if (ctx.literal() != null)
			return visit(ctx.literal());
		return null;
	}

	@Override public ASTNode visitExprList(MxParser.ExprListContext ctx) {
		// return ExprListNode
		ExprListNode ret = new ExprListNode(new ScopeType(ctx), new LocationType(ctx));
		for (var curExprNode : ctx.expr()) {
			ASTNode cur = visit(curExprNode);
			ret.add((ExprNode) cur);
		}
		return ret;
	}

	// TODO
	@Override public ASTNode visitCreator(MxParser.CreatorContext ctx) {
		// return CreatorNode
		return visitChildren(ctx);
	}

	@Override public ASTNode visitPara(MxParser.ParaContext ctx) {
		// return ParaNode
		return new ParaNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(TypeNode) visit(ctx.type()),
				ctx.ID().getText()
		);
	}

	@Override public ASTNode visitParaList(MxParser.ParaListContext ctx) {
		// return ParaListNode
		ParaListNode ret = new ParaListNode(new ScopeType(ctx), new LocationType(ctx));
		for (var curParaNode : ctx.para()) {
			ASTNode cur = visit(curParaNode);
			ret.add((ParaNode) cur);
		}
		return ret;
	}

	@Override public ASTNode visitSimpleType(MxParser.SimpleTypeContext ctx) {
		// return SimpleTypeNode
		if (ctx.BOOL() != null)
			return new SimpleTypeNode(new ScopeType(ctx), new LocationType(ctx), "bool", false);
		if (ctx.INT()!= null)
			return new SimpleTypeNode(new ScopeType(ctx), new LocationType(ctx), "int", false);
		if (ctx.STRING()  != null)
			return new SimpleTypeNode(new ScopeType(ctx), new LocationType(ctx), "string", false);
		if (ctx.ID() != null)
			return new SimpleTypeNode(new ScopeType(ctx), new LocationType(ctx), ctx.ID().getText(), true);
		return null;
	}

	@Override public ASTNode visitType(MxParser.TypeContext ctx) {
		return new TypeNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(SimpleTypeNode) visit(ctx.simpleType()),
				ctx.larr != null
		);
	}
}