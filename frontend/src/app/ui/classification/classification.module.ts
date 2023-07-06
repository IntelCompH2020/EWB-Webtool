import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClassificationComponent } from './classification.component';
import { ClassificationRoutingModule } from './classification-routing.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { CorpusModelModule } from '@common/modules/corpus-model/corpus-model.module';
import { ListingModule } from '@common/modules/listing/listing.module';



@NgModule({
  declarations: [
    ClassificationComponent
  ],
  imports: [
    CommonUiModule,
	ClassificationRoutingModule,
	CorpusModelModule,
	ListingModule
  ]
})
export class ClassificationModule { }
