package com.qgnix.live.news;

import java.io.IOException;

public interface Chain2 {

    Request2 getRequest();

    Response2 getResponse(Request2 request2) throws IOException;

}

