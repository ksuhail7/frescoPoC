import {Component, OnInit} from '@angular/core';
import {Repository} from "./repository";

import {RepositoryService} from './repo.service';
import {Router} from "@angular/router";

@Component({
  selector: 'repo-component',
  templateUrl: './repo.component.html',
  styleUrls: ['./repo.component.css']
})
export class RepositoryComponent implements OnInit {

  constructor(private repoService: RepositoryService) {}
  submitted = false;
  repositories: Repository[];

  model = new Repository("", "");

  addCloseLink: string = "(+) New";

  isCreationInProgress: boolean;

  onSubmit() {
    this.submitted = true;
    this.repoService.create(this.model)
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
    this.repoService
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
