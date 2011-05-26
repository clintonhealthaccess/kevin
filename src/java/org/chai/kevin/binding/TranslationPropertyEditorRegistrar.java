package org.chai.kevin.binding;

import java.util.Map;

import org.chai.kevin.Translation;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

public class TranslationPropertyEditorRegistrar implements PropertyEditorRegistrar {
	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {
	  registry.registerCustomEditor(Translation.class, new TranslationPropertyEditor());
	}
}