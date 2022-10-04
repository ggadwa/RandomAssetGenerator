package com.klinksoftware.rag.character.utility;

import com.klinksoftware.rag.prop.utility.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CharacterInterface {
}
