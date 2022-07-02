package de.fhg.iais.roberta.syntax.sensor;

import java.util.List;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Field;
import de.fhg.iais.roberta.blockly.generated.Hide;
import de.fhg.iais.roberta.blockly.generated.Mutation;
import de.fhg.iais.roberta.factory.BlocklyDropdownFactory;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.sensor.generic.InfraredSensor;
import de.fhg.iais.roberta.transformer.Ast2Jaxb;
import de.fhg.iais.roberta.transformer.Jaxb2Ast;
import de.fhg.iais.roberta.transformer.Jaxb2ProgramAst;
import de.fhg.iais.roberta.util.ast.BlocklyBlockProperties;
import de.fhg.iais.roberta.util.ast.BlocklyComment;
import de.fhg.iais.roberta.util.ast.ExternalSensorBean;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.util.syntax.BlocklyConstants;
import de.fhg.iais.roberta.util.syntax.WithUserDefinedPort;

public abstract class ExternalSensor<V> extends Sensor<V> implements WithUserDefinedPort<V> {
    private final ExternalSensorBean externalSensorBean;

    public ExternalSensor(BlocklyBlockProperties properties, BlocklyComment comment, ExternalSensorBean externalSensorBean) {
        super(properties, comment);
        Assert.notNull(externalSensorBean.getMode());
        Assert.notNull(externalSensorBean.getPort());
        Assert.notNull(externalSensorBean.getSlot());
        this.externalSensorBean = externalSensorBean;
    }

    public String getUserDefinedPort() {
        return externalSensorBean.getPort();
    }

    public String getMode() {
        return externalSensorBean.getMode();
    }

    public String getSlot() {
        return externalSensorBean.getSlot();
    }

    public Mutation getMutation() {
        return externalSensorBean.getMutation();
    }

    public List<Hide> getHide() {
        return externalSensorBean.getHide();
    }

    public ExternalSensorBean getSensorMetaDataBean() {
        return externalSensorBean;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [" + this.getUserDefinedPort() + ", " + this.getMode() + ", " + this.getSlot() + "]";
    }

    public static <V> ExternalSensorBean extractPortAndModeAndSlot(Block block, Jaxb2ProgramAst<V> helper) {
        return extractPortModeSlotMutationHide(block, helper);
    }

    public static <V> ExternalSensorBean extractPortModeSlotMutationHide(Block block, Jaxb2ProgramAst<V> helper) {
        List<Field> fields = Jaxb2Ast.extractFields(block, (short) 3);
        BlocklyDropdownFactory factory = helper.getDropdownFactory();
        String portName = Jaxb2Ast.extractField(fields, BlocklyConstants.SENSORPORT, BlocklyConstants.EMPTY_PORT);
        String modeName = Jaxb2Ast.extractField(fields, BlocklyConstants.MODE, BlocklyConstants.DEFAULT);
        String slotName = Jaxb2Ast.extractField(fields, BlocklyConstants.SLOT, BlocklyConstants.EMPTY_SLOT);
        return new ExternalSensorBean(Jaxb2Ast.sanitizePort(portName), factory.getMode(modeName), Jaxb2Ast.sanitizeSlot(slotName), block.getMutation(), block.getHide());
    }

    @Override
    public Block astToBlock() {
        Block jaxbDestination = new Block();
        Ast2Jaxb.setBasicProperties(this, jaxbDestination);
        if ( !this.getMode().equals(BlocklyConstants.DEFAULT) ) {
            Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.MODE, this.getMode());
        }
        if ( getMutation() != null ) {
            jaxbDestination.setMutation(getMutation());
        }
        List<Hide> hide = getHide();
        if ( hide != null ) {
            jaxbDestination.getHide().addAll(hide);
        }
        String portValue = this.getUserDefinedPort();
        if ( portValue.equals(BlocklyConstants.EMPTY_PORT) ) {
            portValue = "";
        }
        Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.SENSORPORT, portValue);
        String slotValue = this.getSlot();
        if ( slotValue.equals(BlocklyConstants.NO_SLOT) || slotValue.equals(BlocklyConstants.EMPTY_SLOT) ) {
            slotValue = "";
        }
        Ast2Jaxb.addField(jaxbDestination, BlocklyConstants.SLOT, slotValue);
        return jaxbDestination;
    }

    /**
     * TODO: this is an incredible bad design and must be refactored (InfraredSensor as default ... ... )
     */
    public static <V> Phrase<V> jaxbToAst(Block block, Jaxb2ProgramAst<V> helper) {
        ExternalSensorBean sensorData = extractPortAndModeAndSlot(block, helper);
        return new InfraredSensor<V>(Jaxb2Ast.extractBlockProperties(block), Jaxb2Ast.extractComment(block), sensorData);
    }

}
