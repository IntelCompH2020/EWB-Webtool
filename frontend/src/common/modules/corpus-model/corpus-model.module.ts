import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CorpusModelComponent } from './corpus-model.component';
import { CommonUiModule } from '@common/ui/common-ui.module';



@NgModule({
  declarations: [
	CorpusModelComponent
  ],
  imports: [
    CommonUiModule
  ],
  exports: [
	CorpusModelComponent
  ]
})
export class CorpusModelModule { }
