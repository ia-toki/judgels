export interface SourceFile {
  name: string;
  content: any;
}

export interface SubmissionSource {
  files: { [key: string]: SourceFile };
}
