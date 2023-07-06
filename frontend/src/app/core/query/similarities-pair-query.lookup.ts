import { FormBuilder, Validators } from "@angular/forms";

export class SimilatiryPairQuery {
	corpus: string;
    model: string;
    lowerPercent: number;
    higherPercent: number;
    year: number;
    start: number;
    rows: number;

	private formBuilder: FormBuilder = new FormBuilder();

	public createFormGroup() {
		return this.formBuilder.group({
			corpus: [{value: this.corpus, disabled: false}, Validators.required],
			model: [{value: this.model, disabled: false}, Validators.required],
			lowerPercent: [{value: this.lowerPercent, disabled: false}],
			higherPercent: [{value: this.higherPercent, disabled: false}],
			year: [{value: this.year, disabled: false}],
			start: [{value: this.start, disabled: false}],
			rows: [{value: this.rows, disabled: false}]
		});
	}
}
