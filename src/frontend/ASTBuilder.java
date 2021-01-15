package frontend;

import AST.*;
import Util.LocationType;
import Util.ScopeType;
import parser.MxBaseVisitor;
import parser.MxParser;

public class ASTBuilder extends MxBaseVisitor<ASTNode>{
	@Override public ASTNode visitLiteral(MxParser.LiteralContext ctx) {
		if (ctx.TRUE() != null)
			return new BoolLiteralNode(new ScopeType(ctx), new LocationType(ctx), true);
		if (ctx.FALSE()!= null)
			return new BoolLiteralNode(new ScopeType(ctx), new LocationType(ctx), false);
		if (ctx.NUM()  != null)
			return new IntLiteralNode (new ScopeType(ctx), new LocationType(ctx), Integer.parseInt(ctx.NUM().toString()));
		if (ctx.NULL() != null)
			return new NullLiteralNode(new ScopeType(ctx), new LocationType(ctx));
		if (ctx.Str()  != null)
			return new StrLiteralNode (new ScopeType(ctx), new LocationType(ctx), ctx.STR().toString());
	}
    
	@Override public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        return visitChildren(ctx);
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
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitClassDef(MxParser.ClassDefContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitFuncDef(MxParser.FuncDefContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitVarDef(MxParser.VarDefContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitSimpleVarDef(MxParser.SimpleVarDefContext ctx) {
    	return visitChildren(ctx);
    }

	@Override public ASTNode visitConstructorDef(MxParser.ConstructorDefContext ctx) {
    	return visitChildren(ctx);
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
		return visitChildren(ctx);
	}

	@Override public ASTNode visitExprList(MxParser.ExprListContext ctx) {
		return visitChildren(ctx);
	}

	@Override public ASTNode visitCreator(MxParser.CreatorContext ctx) {
		return visitChildren(ctx);
	}

	@Override public ASTNode visitPara(MxParser.ParaContext ctx) {
		return visitChildren(ctx);
	}

	@Override public ASTNode visitParaList(MxParser.ParaListContext ctx) {
		return visitChildren(ctx);
	}

	@Override public ASTNode visitSimpleType(MxParser.SimpleTypeContext ctx) {
		return visitChildren(ctx);
	}

	@Override public ASTNode visitType(MxParser.TypeContext ctx) {
		return visitChildren(ctx);
	}
}