import {Component, OnInit} from '@angular/core';
import {Repository} from "./repository";

import {HomeService} from './home.service';
import {Router} from "@angular/router";

@Component({
  selector: 'home-component',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private homeService: HomeService) {}
  submitted = false;
  repositories: Repository[];

  model = new Repository("", "");

  addCloseLink: string = "(+) New";

  isCreationInProgress: boolean;

  onSubmit() {
    this.submitted = true;
    this.homeService.create(this.model)
    .then(repo => {
      console.debug('repository added ', repo);
      this.repositories.push(repo);
    });
  }

  newRepository() {
    this.model = new Repository("", "");
  }

  ngOnInit(): void {
    this.getAllRepositories();
  }

  getAllRepositories(): void {
    this.homeService
      .getAllRepositories()
      .then(repos => {
        console.debug('repository list ', repos);
        this.repositories = repos});
  }

  addNew() {
    this.isCreationInProgress = !this.isCreationInProgress;
    if(this.isCreationInProgress) {
      this.newRepository();
    }
    this.addCloseLink = this.isCreationInProgress ? "Close" : "(+) New";
  }
}
