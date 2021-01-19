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
		ArrayList<DefNode> defList = new ArrayList<>();
		for (var curDefNode : ctx.def()) {
			defList.add((DefNode) visit(curDefNode));
		}
        return new ProgramNode(new ScopeType(ctx), new LocationType(ctx), defList);
    }
	
    @Override public ASTNode visitSuite(MxParser.SuiteContext ctx) {
		// return SuiteNode
		ArrayList<StmtNode> stmtList = new ArrayList<>();
		for (var curStmt : ctx.stmt()) {
			stmtList.add((StmtNode) visit(curStmt));
		}
        return new SuiteNode(new ScopeType(ctx), new LocationType(ctx), stmtList);
    }
    
	@Override public ASTNode visitBlockStmt(MxParser.BlockStmtContext ctx) {
    	return visit(ctx.suite());
    }

	@Override public ASTNode visitVarDefStmt(MxParser.VarDefStmtContext ctx) {
    	return visit(ctx.varDef());
    }

	@Override public ASTNode visitIfStmt(MxParser.IfStmtContext ctx) {
		// return IfStmtNode
		return new IfStmtNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(StmtNode) visit(ctx.trueStmt),
				(StmtNode) visit(ctx.falseStmt) );
    }

	@Override public ASTNode visitForStmt(MxParser.ForStmtContext ctx) {
		// return ForStmtNode
		return new ForStmtNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(ExprNode) visit(ctx.init),
				(ExprNode) visit(ctx.cond),
				(StmtNode) visit(ctx.stmt()) );
    }

	@Override public ASTNode visitWhileStmt(MxParser.WhileStmtContext ctx) {
		// return WhileStmtNode
		return new WhileStmtNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(ExprNode) visit(ctx.expr()),
				(StmtNode) visit(ctx.stmt()) );
    }

	@Override public ASTNode visitBreakStmt(MxParser.BreakStmtContext ctx) {
		// return BreakNode
    	return new BreakNode(new ScopeType(ctx), new LocationType(ctx));
    }

	@Override public ASTNode visitContinueStmt(MxParser.ContinueStmtContext ctx) {
		// return ContinueNode
		return new ContinueNode(new ScopeType(ctx), new LocationType(ctx));
    }

	@Override public ASTNode visitReturnStmt(MxParser.ReturnStmtContext ctx) {
		// // return ReturnNode
    	if (ctx.expr() != null)
    		return new ReturnNode(new ScopeType(ctx), new LocationType(ctx), (ExprNode) visit(ctx.expr()), true);
    	else
    		return new ReturnNode(new ScopeType(ctx), new LocationType(ctx), null, false);
    }

	@Override public ASTNode visitExprStmt(MxParser.ExprStmtContext ctx) {
    	return visit(ctx.expr());
    }

	@Override public ASTNode visitEmptyStmt(MxParser.EmptyStmtContext ctx) {
    	return null;
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
	    	ret.addVarDef((VarDefNode) visit(curVarDef));
	    }
		for (var curFuncDef : ctx.funcDef()) {
			ret.addFuncDef((FuncDefNode) visit(curFuncDef));
		}
		for (var curConstructorDef : ctx.constructorDef()) {
			ret.addConstructorDef((ConstructorDefNode) visit(curConstructorDef));
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
			ret.addSimpleVarDef((SimpleVarDefNode) visit(curSimpleVarDef));
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
		return visit(ctx.creator());
    }

	@Override public ASTNode visitPrefixExpr(MxParser.PrefixExprContext ctx) {
		// return PrefixExprNode
		return new PrefixExprNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				ctx.op.getText(),
				(ExprNode) visit(ctx.expr()) );
    }

	@Override public ASTNode visitClassMemberExpr(MxParser.ClassMemberExprContext ctx) {
		// return ClassMemberExprNode
		return new ClassMemberExprNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(ExprNode) visit(ctx.expr()),
				ctx.ID().getText() );
    }

	@Override public ASTNode visitSubscriptExpr(MxParser.SubscriptExprContext ctx) {
		// return SubscriptExprNode
		return new SubscriptExprNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(ExprNode) visit(ctx.expr(0)),
				(ExprNode) visit(ctx.expr(1)) );
    }

	@Override public ASTNode visitAtomExpr(MxParser.AtomExprContext ctx) {
    	return visit(ctx.primary());
    }

	@Override public ASTNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
		// return BinaryExprNode
		return new BinaryExprNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				ctx.op.getText(),
				(ExprNode) visit(ctx.expr(0)),
				(ExprNode) visit(ctx.expr(1)) );
    }

	@Override public ASTNode visitPostfixExpr(MxParser.PostfixExprContext ctx) {
		// return PostfixExprNode
		return new PostfixExprNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				ctx.op.getText(),
				(ExprNode) visit(ctx.expr()) );
    }

	@Override public ASTNode visitAssignExpr(MxParser.AssignExprContext ctx) {
		// return AssignExprNode
		return new AssignExprNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(ExprNode) visit(ctx.expr(0)),
				(ExprNode) visit(ctx.expr(1)) );
    }

	@Override public ASTNode visitCallFuncExpr(MxParser.CallFuncExprContext ctx) {
		// return CallFuncExprNode
    	return new CallFuncExprNode (
				new ScopeType(ctx),
				new LocationType(ctx),
				(ExprNode) visit(ctx.expr()),
				(ctx.exprList() != null) ? (ExprListNode) visit(ctx.exprList()) : null );
    }

	@Override public ASTNode visitPrimary(MxParser.PrimaryContext ctx) {
		// expr or return ThisNode or return IDNode or literal
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

	@Override public ASTNode visitSimpleCreator(MxParser.SimpleCreatorContext ctx) {
		// return NewExprNode
		return new NewExprNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(SimpleTypeNode) visit(ctx.simpleType()),
				null,
				0 );
	}

	@Override public ASTNode visitClassCreator(MxParser.ClassCreatorContext ctx) {
		// return NewExprNode
		return new NewExprNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(SimpleTypeNode) visit(ctx.simpleType()),
				null,
				0 );
	}

	@Override public ASTNode visitArrayCreator(MxParser.ArrayCreatorContext ctx) {
		// return NewExprNode
		ArrayList<ExprNode> retExprList = new ArrayList<>();
		for (var curExpr : ctx.expr()) {
			ASTNode cur = visit(curExpr);
			retExprList.add((ExprNode) cur);
		}
		int retDim = 0;
		for (var cur : ctx.children) {
			if (cur.getText().equals("[")) retDim++;
		}
		return new NewExprNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(SimpleTypeNode) visit(ctx.simpleType()),
				retExprList,
				retDim );
	}

	@Override public ASTNode visitWrongCreator(MxParser.WrongCreatorContext ctx) {
		// TODO: throw error
		return null;
	}

	@Override public ASTNode visitPara(MxParser.ParaContext ctx) {
		// return ParaNode
		return new ParaNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(TypeNode) visit(ctx.type()),
				ctx.ID().getText() );
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
		// return TypeNode
		return new TypeNode(
				new ScopeType(ctx),
				new LocationType(ctx),
				(SimpleTypeNode) visit(ctx.simpleType()),
				ctx.larr != null );
	}
}