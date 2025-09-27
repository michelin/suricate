import { DatePipe } from '@angular/common';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

import { appRoutes } from './app.routes';
import { errorInterceptor } from './shared/interceptors/error-interceptor';
import { tokenInterceptor } from './shared/interceptors/token-interceptor';

export const appConfig: ApplicationConfig = {
	providers: [
		provideBrowserGlobalErrorListeners(),
		provideZoneChangeDetection({ eventCoalescing: true }),
		provideHttpClient(withInterceptors([tokenInterceptor, errorInterceptor])),
		provideRouter(appRoutes),
		provideTranslateService({
			loader: provideTranslateHttpLoader({ prefix: './assets/i18n/', suffix: '.json' })
		}),
		DatePipe
	]
};
