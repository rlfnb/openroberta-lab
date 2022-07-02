package de.fhg.iais.roberta.syntax.action.mbed;

import java.util.List;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Field;
import de.fhg.iais.roberta.blockly.generated.Value;
import de.fhg.iais.roberta.mode.action.mbed.DisplayTextMode;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.Ast2Jaxb;
import de.fhg.iais.roberta.transformer.ExprParam;
import de.fhg.iais.roberta.transformer.Jaxb2Ast;
import de.fhg.iais.roberta.transformer.Jaxb2ProgramAst;
import de.fhg.iais.roberta.transformer.forClass.NepoBasic;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.util.ast.BlocklyBlockProperties;
import de.fhg.iais.roberta.util.ast.BlocklyComment;
import de.fhg.iais.roberta.util.syntax.BlocklyConstants;

@NepoBasic(name = "DISPLAY_TEXT_ACTION", category = "ACTOR", blocklyNames = {"mbedActions_display_text"})
public final class DisplayTextAction<V> extends Action<V> {
    public final DisplayTextMode mode;
    public final Expr<V> msg;

    public DisplayTextAction(DisplayTextMode mode, Expr<V> msg, BlocklyBlockProperties properties, BlocklyComment comment) {
        super(properties, comment);
        Assert.isTrue(msg != null && mode != null);
        this.msg = msg;
        this.mode = mode;
        setReadOnly();
    }

    @Override
    public String toString() {
        return "DisplayTextAction [" + this.mode + ", " + this.msg + "]";
    }

    public static <V> Phrase<V> jaxbToAst(Block block, Jaxb2ProgramAst<V> helper) {
        List<Value> values = Jaxb2Ast.extractValues(block, (short) 1);
        List<Field> fields = Jaxb2Ast.extractFields(block, (short) 1);
        Phrase<V> msg = helper.extractValue(values, new ExprParam(BlocklyConstants.OUT, BlocklyType.STRING));
        String displaMode = "";
        try {
            displaMode = Jaxb2Ast.extractField(fields, BlocklyConstants.TYPE);
        } catch ( DbcException e ) {
            displaMode = "TEXT";
        }
        return new DisplayTextAction<>(DisplayTextMode.get(displaMode), Jaxb2Ast.convertPhraseToExpr(msg), Jaxb2Ast.extractBlockProperties(block), Jaxb2Ast.extractComment(block));
    }

    @Override
    public Block astToBlock() {
        Block jaxbDestination = new Block();
        Ast2Jaxb.setBasicProperties(this, jaxbDestination);
        Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.TYPE, this.mode.toString());
        Ast2Jaxb.addValue(jaxbDestination, BlocklyConstants.OUT, this.msg);

        return jaxbDestination;
    }

}
