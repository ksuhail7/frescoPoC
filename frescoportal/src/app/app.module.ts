import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import {RepositoryComponent} from './repository/repo.component';

import {RepositoryService} from './repository/repo.service';
import {AppRoutingModule} from "./app-routing.module";

import './rxjs-extensions';
import {StoreComponent} from "./store/store.component";
import {StoreService} from "./store/store.service";

@NgModule({
  declarations: [
    AppComponent,
    RepositoryComponent,
    StoreComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule
  ],
  providers: [RepositoryService, StoreService],
  bootstrap: [AppComponent]
})
export class AppModule { }
