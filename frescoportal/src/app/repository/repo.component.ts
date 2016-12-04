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

  model = new Repository("", "");

  onSubmit() {
    this.submitted = true;
    this.repoService.create(this.model)
    .then(repo => {
      console.debug('repository added ', repo);
    });
  }

  newRepository() {
    this.model = new Repository("", "");
  }

  ngOnInit(): void {}
}
