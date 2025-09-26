import { DatePipe } from '@angular/common';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

import { appRoutes } from './app.routes';
import { ErrorInterceptor } from './shared/interceptors/error-interceptor';
import { TokenInterceptor } from './shared/interceptors/token-interceptor';

export const appConfig: ApplicationConfig = {
	providers: [
		provideBrowserGlobalErrorListeners(),
		provideZoneChangeDetection({ eventCoalescing: true }),
		provideHttpClient(withInterceptorsFromDi()),
		provideRouter(appRoutes),
		{ provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
		{ provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
		provideTranslateService({
			loader: provideTranslateHttpLoader({ prefix: './assets/i18n/', suffix: '.json' })
		}),
		DatePipe
	]
};
