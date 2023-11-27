package nl.loxia.builder.generator.ap.test.issues._16.differentpackage;

import java.util.ArrayList;
import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;
import nl.loxia.builder.generator.ap.test.issues._16.OuterClassWithTwoLayersOfInnerClasses;

@Builder
public class ReferencingInnerClasses {

    private OuterClassWithTwoLayersOfInnerClasses.InnerClass.InnerInnerClass innerInnerClass;
    private OuterClassWithTwoLayersOfInnerClasses.InnerClass innerClass;
    private OuterClassWithTwoLayersOfInnerClasses outerClass;

    List<OuterClassWithTwoLayersOfInnerClasses.InnerClass.InnerInnerClass> innerInnerClasses = new ArrayList<>();
    List<OuterClassWithTwoLayersOfInnerClasses.InnerClass> innerClasses = new ArrayList<>();
    List<OuterClassWithTwoLayersOfInnerClasses> outerClasses = new ArrayList<>();

    public void setInnerClass(OuterClassWithTwoLayersOfInnerClasses.InnerClass innerClass) {
        this.innerClass = innerClass;
    }

    public void setInnerInnerClass(OuterClassWithTwoLayersOfInnerClasses.InnerClass.InnerInnerClass innerInnerClass) {
        this.innerInnerClass = innerInnerClass;
    }

    public void setOuterClass(OuterClassWithTwoLayersOfInnerClasses outerClass) {
        this.outerClass = outerClass;
    }

    public OuterClassWithTwoLayersOfInnerClasses.InnerClass getInnerClass() {
        return innerClass;
    }

    public OuterClassWithTwoLayersOfInnerClasses.InnerClass.InnerInnerClass getInnerInnerClass() {
        return innerInnerClass;
    }

    public OuterClassWithTwoLayersOfInnerClasses getOuterClass() {
        return outerClass;
    }

    public List<OuterClassWithTwoLayersOfInnerClasses.InnerClass> getInnerClasses() {
        return innerClasses;
    }

    public List<OuterClassWithTwoLayersOfInnerClasses.InnerClass.InnerInnerClass> getInnerInnerClasses() {
        return innerInnerClasses;
    }

    public List<OuterClassWithTwoLayersOfInnerClasses> getOuterClasses() {
        return outerClasses;
    }
}
