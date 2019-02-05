import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { Adal4Service, Adal4HTTPService } from 'adal-angular4';
import { AppComponent } from './app.component';
import { MainComponent } from './main/main.component';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { HomeComponent } from './main/home/home.component';
import { AppRoutingModule } from './app.routing';
import { HttpModule,Http } from '@angular/http';
import { RouterModule } from '@angular/router';
import { AuthenticationGuard } from './shared/guard/authentication-guard';
import { MatNativeDateModule,MatIconModule,MatCardModule,MatProgressSpinnerModule } from '@angular/material';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatMenuModule, MatToolbarModule, MatButtonModule, MatSidenavModule, MatListModule, MatGridListModule, MatTableModule, MatPaginatorModule, MatSortModule } from '@angular/material';
import { LayoutModule } from '@angular/cdk/layout';
import { TableComponent } from './main/table/table.component';
import { HttpClientModule } from  '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    LoginComponent,
    LogoutComponent,
    HomeComponent,
    TableComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule, 
    BrowserAnimationsModule,
    HttpModule, 
    RouterModule,
    MatNativeDateModule,
    ReactiveFormsModule,
    FormsModule,
    MatMenuModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatListModule,
    MatGridListModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    HttpClientModule
  ],
  providers: [Adal4Service, {  
    	        provide: Adal4HTTPService,  
    	        useFactory: Adal4HTTPService.factory,  
              deps: [Http, Adal4Service]
            }, AuthenticationGuard],
  bootstrap: [AppComponent]
})
export class AppModule { }
  
platformBrowserDynamic().bootstrapModule(AppModule);
