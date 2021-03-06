/*
 * Copyright 2004-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jklas.sample.petclinic;

import com.jklas.search.annotations.SearchField;

/**
 * Simple JavaBean domain object adds a name property to <code>Entity</code>.
 * Used as a base class for objects needing these properties.
 * 
 * @author Ken Krebs
 * @author Juergen Hoeller
 */

public class NamedEntity extends Entity {

	@SearchField
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
