package de.fhg.iais.roberta.syntax.lang.functions;

import java.util.ArrayList;
import java.util.List;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Mutation;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.Ast2Jaxb;
import de.fhg.iais.roberta.transformer.ExprParam;
import de.fhg.iais.roberta.transformer.Jaxb2Ast;
import de.fhg.iais.roberta.transformer.Jaxb2ProgramAst;
import de.fhg.iais.roberta.transformer.forClass.NepoBasic;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyBlockProperties;
import de.fhg.iais.roberta.util.ast.BlocklyComment;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.util.syntax.Assoc;
import de.fhg.iais.roberta.util.syntax.BlocklyConstants;
import de.fhg.iais.roberta.util.syntax.FunctionNames;

@NepoBasic(name = "MATH_NUM_PROP_FUNCT", category = "FUNCTION", blocklyNames = {"math_number_property"})
public final class MathNumPropFunct<V> extends Function<V> {
    public final FunctionNames functName;
    public final List<Expr<V>> param;

    public MathNumPropFunct(FunctionNames name, List<Expr<V>> param, BlocklyBlockProperties properties, BlocklyComment comment) {
        super(properties, comment);
        Assert.isTrue(name != null && param != null);
        this.functName = name;
        this.param = param;
        setReadOnly();
    }

    @Override
    public int getPrecedence() {
        return this.functName.getPrecedence();
    }

    @Override
    public Assoc getAssoc() {
        return this.functName.getAssoc();
    }

    @Override
    public BlocklyType getReturnType() {
        return BlocklyType.BOOLEAN;
    }

    @Override
    public String toString() {
        return "MathNumPropFunct [" + this.functName + ", " + this.param + "]";
    }

    public static <V> Phrase<V> jaxbToAst(Block block, Jaxb2ProgramAst<V> helper) {
        boolean divisorInput = block.getMutation().isDivisorInput();
        String op = Jaxb2Ast.extractOperation(block, BlocklyConstants.PROPERTY);
        List<ExprParam> exprParams = new ArrayList<ExprParam>();
        exprParams.add(new ExprParam(BlocklyConstants.NUMBER_TO_CHECK, BlocklyType.NUMBER_INT));

        if ( op.equals(BlocklyConstants.DIVISIBLE_BY) ) {
            Assert.isTrue(divisorInput, "Divisor input is not equal to true!");
            exprParams.add(new ExprParam(BlocklyConstants.DIVISOR, BlocklyType.NUMBER_INT));
        }
        List<Expr<V>> params = helper.extractExprParameters(block, exprParams);
        return new MathNumPropFunct<V>(FunctionNames.get(op), params, Jaxb2Ast.extractBlockProperties(block), Jaxb2Ast.extractComment(block));
    }

    @Override
    public Block astToBlock() {
        Block jaxbDestination = new Block();
        Ast2Jaxb.setBasicProperties(this, jaxbDestination);

        Mutation mutation = new Mutation();
        mutation.setDivisorInput(false);
        Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.PROPERTY, this.functName.name());
        Ast2Jaxb.addValue(jaxbDestination, BlocklyConstants.NUMBER_TO_CHECK, this.param.get(0));
        if ( this.functName == FunctionNames.DIVISIBLE_BY ) {
            Ast2Jaxb.addValue(jaxbDestination, BlocklyConstants.DIVISOR, this.param.get(1));
            mutation.setDivisorInput(true);
        }
        jaxbDestination.setMutation(mutation);
        return jaxbDestination;
    }
}
