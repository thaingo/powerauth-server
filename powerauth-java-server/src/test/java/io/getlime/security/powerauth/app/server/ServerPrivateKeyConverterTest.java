/*
 * PowerAuth Server and related software components
 * Copyright (C) 2018 Wultra s.r.o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.getlime.security.powerauth.app.server;

import com.google.common.io.BaseEncoding;
import io.getlime.security.powerauth.app.server.converter.v3.ServerPrivateKeyConverter;
import io.getlime.security.powerauth.app.server.database.model.EncryptionMode;
import io.getlime.security.powerauth.app.server.database.model.ServerPrivateKey;
import io.getlime.security.powerauth.app.server.service.exceptions.GenericServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for encryption and decryption of server private keys.
 *
 * @author Roman Strobl, roman.strobl@wultra.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ServerPrivateKeyConverterTest {

    private static final String SERVER_PRIVATE_KEY_PLAIN = "YAJ1A/QtTTB33R3Xnx3q7+QFuth6cRagtCMGTytV9VE=";

    private ServerPrivateKeyConverter serverPrivateKeyConverter;

    @Autowired
    public void setServerPrivateKeyConverter(ServerPrivateKeyConverter serverPrivateKeyConverter) {
        this.serverPrivateKeyConverter = serverPrivateKeyConverter;
    }

    @Test
    public void testFromDbValueNoEncryption() throws Exception {
        final ServerPrivateKey serverPrivateKeyEncrypted = new ServerPrivateKey(EncryptionMode.NO_ENCRYPTION, SERVER_PRIVATE_KEY_PLAIN);
        String serverPrivateKeyActual = serverPrivateKeyConverter.fromDBValue(serverPrivateKeyEncrypted, "test", "015286e0-e1c5-4ee1-8d1b-c6947cab0a56");
        assertEquals(SERVER_PRIVATE_KEY_PLAIN, serverPrivateKeyActual);
    }

    @Test
    public void testEncryptionAndDecryptionSuccess() throws Exception {
        byte[] serverPrivateKeyBytes = BaseEncoding.base64().decode(SERVER_PRIVATE_KEY_PLAIN);
        ServerPrivateKey serverPrivateKeyEncrypted = serverPrivateKeyConverter.toDBValue(serverPrivateKeyBytes,"test", "015286e0-e1c5-4ee1-8d1b-c6947cab0a56");
        String serverPrivateKeyActual = serverPrivateKeyConverter.fromDBValue(serverPrivateKeyEncrypted, "test", "015286e0-e1c5-4ee1-8d1b-c6947cab0a56");
        assertEquals(SERVER_PRIVATE_KEY_PLAIN, serverPrivateKeyActual);
    }

    @Test
    public void testEncryptionAndDecryptionDifferentUserFail() {
        assertThrows(GenericServiceException.class, ()-> {
            byte[] serverPrivateKeyBytes = BaseEncoding.base64().decode(SERVER_PRIVATE_KEY_PLAIN);
            ServerPrivateKey serverPrivateKeyEncrypted = serverPrivateKeyConverter.toDBValue(serverPrivateKeyBytes, "test", "015286e0-e1c5-4ee1-8d1b-c6947cab0a56");
            serverPrivateKeyConverter.fromDBValue(serverPrivateKeyEncrypted, "test2", "015286e0-e1c5-4ee1-8d1b-c6947cab0a56");
        });
    }

    @Test
    public void testEncryptionAndDecryptionDifferentActivationFailServerPrivateKeyConverter() {
        assertThrows(GenericServiceException.class, ()-> {
            byte[] serverPrivateKeyBytes = BaseEncoding.base64().decode(SERVER_PRIVATE_KEY_PLAIN);
            ServerPrivateKey serverPrivateKeyEncrypted = serverPrivateKeyConverter.toDBValue(serverPrivateKeyBytes, "test", "015286e0-e1c5-4ee1-8d1b-c6947cab0a56");
            serverPrivateKeyConverter.fromDBValue(serverPrivateKeyEncrypted, "test", "115286e0-e1c5-4ee1-8d1b-c6947cab0a56");
        });
    }

}
