import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import {HomeComponent} from './home/home.component';
import {RepositoryComponent} from './repository/repository.component';

import {HomeService} from './home/home.service';
import {AppRoutingModule} from "./app-routing.module";

import './rxjs-extensions';
import {StoreComponent} from "./store/store.component";
import {StoreService} from "./store/store.service";

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    StoreComponent,
    RepositoryComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule,
    RouterModule.forRoot([
{
  path: 'repository',
  component: RepositoryComponent
},
{
  path: 'store',
  component: StoreComponent
}
 ])
  ],
  providers: [HomeService, StoreService],
  bootstrap: [AppComponent]
})
export class AppModule { }
