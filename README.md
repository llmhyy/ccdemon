# CCDemon
A Clone-Based Approach for Recommending Modification on Pasted Code

![Snapshot of CCDemon](/ccdemon/image/screenshot.jpg?raw=true "Snapshot of Microbat")

## Introduction
This project is built for recommending the to-be-modified code slot and content on a pasted code. When a user copies a piece of code in projects, we consider its cloned code as historical result of copy-and-paste practice and the clone difference as historical result of modification on the pasted code. We mine the modification rules from clone differences. With these rules, CCDemon can even dynamically adjust the modification recommendations when programmers are modifying the pasted code. More details can be checked in the following paper:

Yun Lin, Xin Peng, Zhenchang Xing, Diwen Zheng, and Wenyun Zhao. 2015. Clone-based and interactive recommendation for modifying pasted code. In Proceedings of the 2015 10th Joint Meeting on Foundations of Software Engineering (ESEC/FSE 2015). ACM, New York, NY, USA, 520-531. DOI=http://dx.doi.org/10.1145/2786805.2786871

## Dependency
The project depends on [Clonepedia](https://github.com/llmhyy/clonepedia), [MCIDiff](https://github.com/llmhyy/mcidiff), and [Data Mining](https://github.com/llmhyy/data_mining) code repository. Please refer to the link for the configuration of MCIDiff.

## Set Up
When using CCDemon, users have to keep two things in mind:
1) transfer the line delimiter of Java files to windows style (i.e., \r\n);
2) forbid Eclipse's default code indentation function by Windows>>Preferences>>Java>>Editor>>Typing>>unselect "Adjust indentation" 

## Contact
If you have any problem on using our code, please feel free to contact me by: llmhyy@gmail.com or linyun@fudan.edu.cn. You can also contact Prof. Xin Peng (pengxin@fudan.edu.cn) or Prof. Zhenchang Xing (zcxing@ntu.edu.sg) for more information.


