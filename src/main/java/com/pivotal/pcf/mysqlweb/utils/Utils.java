/*
PivotalMySQLWeb

Copyright (c) 2017-Present Pivotal Software, Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.pivotal.pcf.mysqlweb.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

public class Utils
{
    public static String getVcapServices ()
    {
        String jsonString = null;
        jsonString = System.getenv().get("VCAP_SERVICES");
        return jsonString;
    }

    public static String applicationIndex ()
    {
        String instanceIndex = "N/A";

        try
        {
            instanceIndex = getVcapApplicationMap().getOrDefault("instance_index", "N/A").toString();
        }
        catch (Exception ex)
        {

        }

        return instanceIndex;
    }

    static private Map getVcapApplicationMap() throws Exception {
        return getEnvMap("VCAP_APPLICATION");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map getEnvMap(String vcap) throws Exception {
        String vcapEnv = System.getenv(vcap);
        ObjectMapper mapper = new ObjectMapper();

        if (vcapEnv != null) {
            Map<String, ?> vcapMap = mapper.readValue(vcapEnv, Map.class);
            return vcapMap;
        }

        return new HashMap<String, String>();
    }

    public static Map<String, String> jvmPropertyMap ()
    {
        Properties props = System.getProperties();
        Map<String, String> map = new HashMap<String, String>((Map) props);

        return map;
    }
}
