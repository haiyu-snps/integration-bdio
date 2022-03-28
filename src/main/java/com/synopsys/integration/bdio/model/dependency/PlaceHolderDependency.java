package com.synopsys.integration.bdio.model.dependency;

import java.util.UUID;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class PlaceHolderDependency extends ProjectDependency {
    public PlaceHolderDependency() {
        super(ExternalIdFactory.STATIC.createModuleNamesExternalId(Forge.GITHUB, UUID.randomUUID().toString()));
    }
}
