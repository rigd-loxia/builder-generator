package nl.loxia.builder.generator.ap;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.AbstractAnnotationValueVisitor9;

final class AnnotationClassReferenceVisitor extends AbstractAnnotationValueVisitor9<Void, Void> {

    final List<TypeMirror> mirrors = new ArrayList<>();

    @Override
    public Void visitBoolean(boolean b, Void p) {
        return null;
    }

    @Override
    public Void visitByte(byte b, Void p) {
        return null;
    }

    @Override
    public Void visitChar(char c, Void p) {
        return null;
    }

    @Override
    public Void visitDouble(double d, Void p) {
        return null;
    }

    @Override
    public Void visitFloat(float f, Void p) {
        return null;
    }

    @Override
    public Void visitInt(int i, Void p) {
        return null;
    }

    @Override
    public Void visitLong(long i, Void p) {
        return null;
    }

    @Override
    public Void visitShort(short s, Void p) {
        return null;
    }

    @Override
    public Void visitString(String s, Void p) {
        return null;
    }

    @Override
    public Void visitType(TypeMirror t, Void p) {
        mirrors.add(t);
        return null;
    }

    @Override
    public Void visitEnumConstant(VariableElement c, Void p) {
        return null;
    }

    @Override
    public Void visitAnnotation(AnnotationMirror a, Void p) {
        return null;
    }

    @Override
    public Void visitArray(List<? extends AnnotationValue> vals, Void p) {
        for (AnnotationValue annotationValue : vals) {
            annotationValue.accept(this, p);
        }
        return null;
    }
}