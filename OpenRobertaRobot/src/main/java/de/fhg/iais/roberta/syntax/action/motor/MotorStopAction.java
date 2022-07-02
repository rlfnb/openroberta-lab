package de.fhg.iais.roberta.syntax.action.motor;

import java.util.List;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Field;
import de.fhg.iais.roberta.factory.BlocklyDropdownFactory;
import de.fhg.iais.roberta.inter.mode.action.IMotorStopMode;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.MoveAction;
import de.fhg.iais.roberta.transformer.Ast2Jaxb;
import de.fhg.iais.roberta.transformer.Jaxb2Ast;
import de.fhg.iais.roberta.transformer.Jaxb2ProgramAst;
import de.fhg.iais.roberta.transformer.forClass.NepoBasic;
import de.fhg.iais.roberta.util.ast.BlocklyBlockProperties;
import de.fhg.iais.roberta.util.ast.BlocklyComment;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.util.syntax.BlocklyConstants;

@NepoBasic(name = "MOTOR_STOP_ACTION", category = "ACTOR", blocklyNames = {"makeblockActions_motor_stop", "sim_motor_stop", "mbedActions_motor_stop", "robActions_motor_stop"})
public final class MotorStopAction<V> extends MoveAction<V> {
    public final IMotorStopMode mode;

    public MotorStopAction(String port, IMotorStopMode mode, BlocklyBlockProperties properties, BlocklyComment comment) {
        super(properties, comment, port);
        Assert.isTrue(port != null);
        this.mode = mode;
        setReadOnly();
    }

    @Override
    public String toString() {
        if ( this.mode != null ) {
            return "MotorStop [port=" + getUserDefinedPort() + ", mode=" + this.mode + "]";
        } else {
            return "MotorStop [port=" + getUserDefinedPort() + "]";
        }
    }

    public static <V> Phrase<V> jaxbToAst(Block block, Jaxb2ProgramAst<V> helper) {
        BlocklyDropdownFactory factory = helper.getDropdownFactory();
        List<Field> fields = Jaxb2Ast.extractFields(block, (short) 2);
        String portName = Jaxb2Ast.extractField(fields, BlocklyConstants.MOTORPORT);
        if ( fields.size() > 1 ) {
            String modeName = Jaxb2Ast.extractField(fields, BlocklyConstants.MODE);
            return new MotorStopAction<V>(Jaxb2Ast.sanitizePort(portName), factory.getMotorStopMode(modeName), Jaxb2Ast.extractBlockProperties(block), Jaxb2Ast.extractComment(block));

        }
        return new MotorStopAction<V>(Jaxb2Ast.sanitizePort(portName), null, Jaxb2Ast.extractBlockProperties(block), Jaxb2Ast.extractComment(block));
    }

    @Override
    public Block astToBlock() {
        Block jaxbDestination = new Block();
        Ast2Jaxb.setBasicProperties(this, jaxbDestination);

        Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.MOTORPORT, getUserDefinedPort().toString());
        if ( this.mode != null ) {
            Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.MODE, this.mode.toString());
        }

        return jaxbDestination;
    }
}
