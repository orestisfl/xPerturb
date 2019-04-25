; ModuleID = 'challenge.c'
source_filename = "challenge.c"
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@BLOCK_SIZE = constant i32 16, align 4
@.str = private unnamed_addr constant [51 x i8] c"Usage:\0A %s <16 bytes in hex, separated by spaces>\0A\00", align 1
@.str.1 = private unnamed_addr constant [12 x i8] c"INPUT:     \00", align 1
@.str.2 = private unnamed_addr constant [6 x i8] c"%02x \00", align 1
@.str.3 = private unnamed_addr constant [13 x i8] c"\0AOUTPUT:    \00", align 1
@.str.4 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1

; Function Attrs: noinline nounwind optnone uwtable
define i32 @main(i32, i8**) #0 {
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  %5 = alloca i8**, align 8
  %6 = alloca [16 x i8], align 16
  %7 = alloca [16 x i8], align 16
  %8 = alloca i32, align 4
  store i32 0, i32* %3, align 4
  store i32 %0, i32* %4, align 4
  store i8** %1, i8*** %5, align 8
  %9 = load i32, i32* %4, align 4
  %10 = icmp ne i32 %9, 17
  br i1 %10, label %11, label %16

; <label>:11:                                     ; preds = %2
  %12 = load i8**, i8*** %5, align 8
  %13 = getelementptr inbounds i8*, i8** %12, i64 0
  %14 = load i8*, i8** %13, align 8
  %15 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([51 x i8], [51 x i8]* @.str, i32 0, i32 0), i8* %14)
  store i32 1, i32* %3, align 4
  br label %69

; <label>:16:                                     ; preds = %2
  store i32 0, i32* %8, align 4
  br label %17

; <label>:17:                                     ; preds = %32, %16
  %18 = load i32, i32* %8, align 4
  %19 = icmp slt i32 %18, 16
  br i1 %19, label %20, label %35

; <label>:20:                                     ; preds = %17
  %21 = load i8**, i8*** %5, align 8
  %22 = load i32, i32* %8, align 4
  %23 = add nsw i32 %22, 1
  %24 = sext i32 %23 to i64
  %25 = getelementptr inbounds i8*, i8** %21, i64 %24
  %26 = load i8*, i8** %25, align 8
  %27 = call i64 @strtoul(i8* %26, i8** null, i32 16) #3
  %28 = trunc i64 %27 to i8
  %29 = load i32, i32* %8, align 4
  %30 = sext i32 %29 to i64
  %31 = getelementptr inbounds [16 x i8], [16 x i8]* %6, i64 0, i64 %30
  store i8 %28, i8* %31, align 1
  br label %32

; <label>:32:                                     ; preds = %20
  %33 = load i32, i32* %8, align 4
  %34 = add nsw i32 %33, 1
  store i32 %34, i32* %8, align 4
  br label %17

; <label>:35:                                     ; preds = %17
  %36 = getelementptr inbounds [16 x i8], [16 x i8]* %6, i32 0, i32 0
  %37 = getelementptr inbounds [16 x i8], [16 x i8]* %7, i32 0, i32 0
  call void @chow_aes3_encrypt_wb(i8* %36, i8* %37)
  %38 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str.1, i32 0, i32 0))
  store i32 0, i32* %8, align 4
  br label %39

; <label>:39:                                     ; preds = %49, %35
  %40 = load i32, i32* %8, align 4
  %41 = icmp slt i32 %40, 16
  br i1 %41, label %42, label %52

; <label>:42:                                     ; preds = %39
  %43 = load i32, i32* %8, align 4
  %44 = sext i32 %43 to i64
  %45 = getelementptr inbounds [16 x i8], [16 x i8]* %6, i64 0, i64 %44
  %46 = load i8, i8* %45, align 1
  %47 = zext i8 %46 to i32
  %48 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str.2, i32 0, i32 0), i32 %47)
  br label %49

; <label>:49:                                     ; preds = %42
  %50 = load i32, i32* %8, align 4
  %51 = add nsw i32 %50, 1
  store i32 %51, i32* %8, align 4
  br label %39

; <label>:52:                                     ; preds = %39
  %53 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.str.3, i32 0, i32 0))
  store i32 0, i32* %8, align 4
  br label %54

; <label>:54:                                     ; preds = %64, %52
  %55 = load i32, i32* %8, align 4
  %56 = icmp slt i32 %55, 16
  br i1 %56, label %57, label %67

; <label>:57:                                     ; preds = %54
  %58 = load i32, i32* %8, align 4
  %59 = sext i32 %58 to i64
  %60 = getelementptr inbounds [16 x i8], [16 x i8]* %7, i64 0, i64 %59
  %61 = load i8, i8* %60, align 1
  %62 = zext i8 %61 to i32
  %63 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str.2, i32 0, i32 0), i32 %62)
  br label %64

; <label>:64:                                     ; preds = %57
  %65 = load i32, i32* %8, align 4
  %66 = add nsw i32 %65, 1
  store i32 %66, i32* %8, align 4
  br label %54

; <label>:67:                                     ; preds = %54
  %68 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.4, i32 0, i32 0))
  store i32 0, i32* %3, align 4
  br label %69

; <label>:69:                                     ; preds = %67, %11
  %70 = load i32, i32* %3, align 4
  ret i32 %70
}

declare i32 @printf(i8*, ...) #1

; Function Attrs: nounwind
declare i64 @strtoul(i8*, i8**, i32) #2

declare void @chow_aes3_encrypt_wb(i8*, i8*) #1

attributes #0 = { noinline nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #2 = { nounwind "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #3 = { nounwind }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 6.0.0-1ubuntu2 (tags/RELEASE_600/final)"}
