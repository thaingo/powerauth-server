/*
 * PowerAuth Server and related software components
 * Copyright (C) 2019 Wultra s.r.o.
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

import io.getlime.security.powerauth.app.server.database.model.entity.ApplicationEntity;
import io.getlime.security.powerauth.app.server.service.exceptions.GenericServiceException;
import io.getlime.security.powerauth.app.server.service.v3.PowerAuthService;
import io.getlime.security.powerauth.v3.CreateApplicationRequest;
import io.getlime.security.powerauth.v3.CreateApplicationResponse;
import io.getlime.security.powerauth.v3.GetApplicationDetailRequest;
import io.getlime.security.powerauth.v3.GetApplicationDetailResponse;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for getting application detail.
 *
 * @author Lukas Lukovsky, lukas.lukovsky@gmail.com
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class GetApplicationDetailTest {

    private PowerAuthService powerAuthService;

    @Autowired
    public void setPowerAuthService(PowerAuthService powerAuthService) {
        this.powerAuthService = powerAuthService;
    }

    @Test
    public void testGetApplicationDetailByExistingId() throws Exception {
        ApplicationEntity application = createApplication();
        GetApplicationDetailRequest request = new GetApplicationDetailRequest();
        request.setApplicationId(application.getId());

        GetApplicationDetailResponse response = powerAuthService.getApplicationDetail(request);
        assertEquals(application.getId(), Long.valueOf(response.getApplicationId()));
        assertEquals(application.getName(), response.getApplicationName());
    }

    @Test
    public void testGetApplicationDetailByNotExistingId() {
        GetApplicationDetailRequest request = new GetApplicationDetailRequest();
        request.setApplicationId(Long.MAX_VALUE);
        assertThrows(GenericServiceException.class, ()-> powerAuthService.getApplicationDetail(request));
    }

    @Test
    public void testGetApplicationDetailByExistingName() throws Exception {
        ApplicationEntity application = createApplication();
        GetApplicationDetailRequest request = new GetApplicationDetailRequest();
        request.setApplicationName(application.getName());

        GetApplicationDetailResponse response = powerAuthService.getApplicationDetail(request);
        assertEquals(application.getId(), Long.valueOf(response.getApplicationId()));
        assertEquals(application.getName(), response.getApplicationName());
    }

    @Test
    public void testGetApplicationDetailByNotExistingName() {
        GetApplicationDetailRequest request = new GetApplicationDetailRequest();
        request.setApplicationName("NOT_EXISTING_NAME");
        assertThrows(GenericServiceException.class, ()-> powerAuthService.getApplicationDetail(request));
    }

    @Test
    public void testGetApplicationDetailInvalidRequest() throws Exception {
        ApplicationEntity application = createApplication();
        GetApplicationDetailRequest request = new GetApplicationDetailRequest();
        request.setApplicationId(application.getId());
        request.setApplicationName(application.getName());
        assertThrows(GenericServiceException.class, ()-> powerAuthService.getApplicationDetail(request));
    }

    private ApplicationEntity createApplication() throws Exception {
        return createApplication("GetApplicationDetailTest-" + System.currentTimeMillis());
    }

    private ApplicationEntity createApplication(String applicationName) throws Exception {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setApplicationName(applicationName);
        CreateApplicationResponse response = powerAuthService.createApplication(request);
        return new ApplicationEntity(response.getApplicationId(), response.getApplicationName(), Lists.emptyList());
    }

}
