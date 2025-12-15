package HooYah.Gateway;

import HooYah.Gateway.conf.Config;
import HooYah.Gateway.conf.DbConfig;
import HooYah.Gateway.conf.EnvConfig;
import HooYah.Gateway.conf.RouteConfig;
import HooYah.Gateway.conf.WebConfig;
import HooYah.Gateway.router.Router;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@ComponentScan(basePackages = "HooYah.Gateway")
@EnableAutoConfiguration
public class YachtApplication {

	public static void main(String[] args) {
		// Spring 컨텍스트 생성
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		
		// 설정 클래스 등록
		context.register(
            RouteConfig.class,
			Config.class,
			DbConfig.class,
			WebConfig.class,
			EnvConfig.class,
			Router.class
		);

		// 컨텍스트 새로고침
		context.refresh();
		
		// HttpHandler 생성 (Spring Cloud Gateway 사용)
		HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(context).build();
		
		// Netty 서버 시작
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer httpServer = HttpServer.create()
			.port(9090)
			.handle(adapter);
		
		DisposableServer server = httpServer.bindNow();
		
		System.out.println("Gateway 서버가 포트 9090에서 시작되었습니다.");
		
		// 종료 시그널 처리
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			server.disposeNow();
			context.close();
		}));
		
		// 서버가 종료될 때까지 대기
		server.onDispose().block();
	}

}
