package HooYah.Gateway.checker.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import java.util.Map;

public class DockerStatusChecker extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final ObjectMapper objectMapper;

    public DockerStatusChecker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // send request
        ctx.writeAndFlush("").addListener(f -> {
            if (f.isSuccess()) {
                ctx.read();
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // read response
        String responseStr = msg.content().toString(CharsetUtil.UTF_8); // String json

        Map response =  objectMapper.readValue(responseStr, Map.class);


    }

    /*
    /containers/{id}/json
    {
    "Status": "running",
    "Running": true,
    "Paused": false,
    "Restarting": false,
    "OOMKilled": false,
    "Dead": false,
    "Pid": 1234,
    "ExitCode": 0,
    "Error": "",
    "StartedAt": "2023-10-27T01:23:45.678Z",
    "FinishedAt": "0001-01-01T00:00:00Z",
    "Health": {
        "Status": "healthy",
        "FailingStreak": 0,
        "Log": [...]
    }
    }

    /containers/{id}/stats?stream=false
{
  "read": "2026-01-14T08:05:00Z",
  "memory_stats": {
    "usage": 524288000,      // 현재 사용량 (bytes)
    "limit": 1073741824,     // 할당 제한량 (bytes)
    "stats": { "cache": 1024 }
  },
  "cpu_stats": {
    "cpu_usage": {
      "total_usage": 150000000,
      "percpu_usage": [75000000, 75000000]
    },
    "system_cpu_usage": 4000000000,
    "online_cpus": 2
  },
  "precpu_stats": {          // 계산을 위한 직전 값
    "cpu_usage": { "total_usage": 140000000 },
    "system_cpu_usage": 3900000000
  },
  "pids_stats": { "current": 45 } // 현재 실행 중인 스레드/프로세스 수
}
     */

}
